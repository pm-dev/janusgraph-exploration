package com.coxautodev.graphql.tools

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.common.collect.Maps
import graphql.language.Definition
import graphql.language.FieldDefinition import graphql.language.InputObjectTypeDefinition
import graphql.language.InputValueDefinition
import graphql.language.InterfaceTypeDefinition
import graphql.language.ObjectTypeDefinition
import graphql.language.ScalarTypeDefinition
import graphql.language.SchemaDefinition
import graphql.language.TypeDefinition
import graphql.language.TypeExtensionDefinition
import graphql.language.TypeName
import graphql.language.UnionTypeDefinition
import graphql.schema.GraphQLScalarType
import graphql.schema.idl.ScalarInfo
import org.slf4j.LoggerFactory
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * @author Andrew Potter
 */
internal class SchemaClassScanner(initialDictionary: BiMap<String, Class<*>>, allDefinitions: List<Definition>, resolvers: List<GraphQLResolver<*>>, private val scalars: CustomScalarMap, private val options: SchemaParserOptions) {

    companion object {
        val log = LoggerFactory.getLogger(SchemaClassScanner::class.java)!!
    }

    private val rootInfo = RootTypeInfo.fromSchemaDefinitions(allDefinitions.filterIsInstance<SchemaDefinition>())

    private val queryResolvers = resolvers.filterIsInstance<GraphQLQueryResolver>()
    private val mutationResolvers = resolvers.filterIsInstance<GraphQLMutationResolver>()
    private val subscriptionResolvers = resolvers.filterIsInstance<GraphQLSubscriptionResolver>()

    private val resolverInfos = resolvers.minus(queryResolvers).minus(mutationResolvers).minus(subscriptionResolvers).map { NormalResolverInfo(it, options) }
    private val resolverInfosByDataClass = this.resolverInfos.associateBy { it.dataClassType }

    private val initialDictionary = initialDictionary.mapValues { InitialDictionaryEntry(it.value) }
    private val extensionDefinitions = allDefinitions.filterIsInstance<TypeExtensionDefinition>()

    private val definitionsByName = (allDefinitions.filterIsInstance<TypeDefinition>() - extensionDefinitions).associateBy { it.name }
    private val objectDefinitions = (allDefinitions.filterIsInstance<ObjectTypeDefinition>() - extensionDefinitions)
    private val objectDefinitionsByName = objectDefinitions.associateBy { it.name }
    private val interfaceDefinitionsByName = allDefinitions.filterIsInstance<InterfaceTypeDefinition>().associateBy { it.name }

    private val fieldResolverScanner = FieldResolverScanner(options)
    private val typeClassMatcher = TypeClassMatcher(definitionsByName)
    private val dictionary = mutableMapOf<TypeDefinition, DictionaryEntry>()
    private val unvalidatedTypes = mutableSetOf<TypeDefinition>()
    private val queue = linkedSetOf<QueueItem>()

    private val fieldResolversByType = mutableMapOf<ObjectTypeDefinition, MutableMap<FieldDefinition, FieldResolver>>()

    init {
        initialDictionary.forEach { (name, clazz) ->
            if(!definitionsByName.containsKey(name)) {
                throw SchemaClassScannerError("Class in supplied dictionary '${clazz.name}' specified type name '$name', but a type definition with that name was not found!")
            }
        }

        if(options.allowUnimplementedResolvers) {
            log.warn("Option 'allowUnimplementedResolvers' should only be set to true during development, as it can cause schema errors to be moved to query time instead of schema creation time.  Make sure this is turned off in production.")
        }
    }

    /**
     * Attempts to discover GraphQL Type -> Java Class relationships by matching return types/argument types on known fields
     */
    fun scanForClasses(): ScannedSchemaObjects {

        // Figure out what query, mutation and subscription types are called
        val rootTypeHolder = RootTypesHolder(options, rootInfo, definitionsByName, queryResolvers, mutationResolvers, subscriptionResolvers)

        handleRootType(rootTypeHolder.query)
        handleRootType(rootTypeHolder.mutation)
        handleRootType(rootTypeHolder.subscription)

        scanQueue()

        // Loop over all objects scanning each one only once for more objects to discover.
        do {
            do {
                // Require all implementors of discovered interfaces to be discovered or provided.
                handleInterfaceOrUnionSubTypes(getAllObjectTypesImplementingDiscoveredInterfaces(), { "Object type '${it.name}' implements a known interface, but no class could be found for that type name.  Please pass a class for type '${it.name}' in the parser's dictionary." })
            } while (scanQueue())

            // Require all members of discovered unions to be discovered.
            handleInterfaceOrUnionSubTypes(getAllObjectTypeMembersOfDiscoveredUnions(), { "Object type '${it.name}' is a member of a known union, but no class could be found for that type name.  Please pass a class for type '${it.name}' in the parser's dictionary." })
        } while (scanQueue())

        return validateAndCreateResult(rootTypeHolder)
    }

    private fun scanQueue(): Boolean {
        if(queue.isEmpty()) {
            return false
        }

        while (queue.isNotEmpty()) {
            scanQueueItemForPotentialMatches(queue.iterator().run { val item = next(); remove(); item })
        }

        return true
    }

    /**
     * Adds all root resolvers for a type to the list of classes to scan
     */
    private fun handleRootType(rootType: RootType?) {
        if(rootType == null) {
            return
        }

        unvalidatedTypes.add(rootType.type)
        scanInterfacesOfType(rootType.type)
        scanResolverInfoForPotentialMatches(rootType.type, rootType.resolverInfo)
    }

    private fun validateAndCreateResult(rootTypeHolder: RootTypesHolder): ScannedSchemaObjects {
        initialDictionary.filter { !it.value.accessed }.forEach {
            log.warn("Dictionary mapping was provided but never used, and can be safely deleted: \"${it.key}\" -> ${it.value.get().name}")
        }

        val observedDefinitions = dictionary.keys.toSet() + unvalidatedTypes

        // The dictionary doesn't need to know what classes are used with scalars.
        // In addition, scalars can have duplicate classes so that breaks the bi-map.
        // Input types can also be excluded from the dictionary, since it's only used for interfaces, unions, and enums.
        // Union types can also be excluded, as their possible types are resolved recursively later
        val dictionary = try {
            Maps.unmodifiableBiMap(HashBiMap.create<TypeDefinition, Class<*>>().also {
                dictionary.filter { it.value.typeClass != null && it.key !is InputObjectTypeDefinition && it.key !is UnionTypeDefinition}.mapValuesTo(it) { it.value.typeClass }
            })
        } catch (t: Throwable) {
            throw SchemaClassScannerError("Error creating bimap of type => class", t)
        }
        val scalarDefinitions = observedDefinitions.filterIsInstance<ScalarTypeDefinition>()

        // Ensure all scalar definitions have implementations and add the definition to those.
        val scalars = scalarDefinitions.filter { !ScalarInfo.STANDARD_SCALAR_DEFINITIONS.containsKey(it.name) }.map { definition ->
            val provided = scalars[definition.name] ?: throw SchemaClassScannerError("Expected a user-defined GraphQL scalar type with name '${definition.name}' but found none!")
            GraphQLScalarType(provided.name, SchemaParser.getDocumentation(definition) ?: provided.description, provided.coercing, definition)
        }.associateBy { it.name!! }

        (definitionsByName.values - observedDefinitions).forEach { definition ->
            log.warn("Schema type was defined but can never be accessed, and can be safely deleted: ${definition.name}")
        }

        val fieldResolvers = fieldResolversByType.flatMap { it.value.map { it.value } }
        val observedNormalResolverInfos = fieldResolvers.map { it.resolverInfo }.distinct().filterIsInstance<NormalResolverInfo>()

        (resolverInfos - observedNormalResolverInfos).forEach { resolverInfo ->
            log.warn("Resolver was provided but no methods on it were used in data fetchers, and can be safely deleted: ${resolverInfo.resolver}")
        }

        validateRootResolversWereUsed(rootTypeHolder.query, fieldResolvers)
        validateRootResolversWereUsed(rootTypeHolder.mutation, fieldResolvers)
        validateRootResolversWereUsed(rootTypeHolder.subscription, fieldResolvers)

        return ScannedSchemaObjects(dictionary, observedDefinitions + extensionDefinitions, scalars, rootInfo, fieldResolversByType.toMap())
    }

    private fun validateRootResolversWereUsed(rootType: RootType?, fieldResolvers: List<FieldResolver>) {
        if(rootType == null) {
            return
        }

        val observedRootTypes = fieldResolvers.filter { it.resolverInfo is RootResolverInfo && it.resolverInfo == rootType.resolverInfo }.map { it.search.type }.toSet()
        rootType.resolvers.forEach { resolver ->
            if(rootType.resolverInfo.getRealResolverClass(resolver, options) !in observedRootTypes) {
                log.warn("Root ${rootType.name} resolver was provided but no methods on it were used in data fetchers for GraphQL type '${rootType.type.name}'!  Either remove the ${rootType.resolverInterface.name} interface from the resolver or remove the resolver entirely: $resolver")
            }
        }
    }

    private fun getAllObjectTypesImplementingDiscoveredInterfaces(): List<ObjectTypeDefinition> {
        return dictionary.keys.filterIsInstance<InterfaceTypeDefinition>().map { iface ->
            objectDefinitions.filter { obj -> obj.implements.filterIsInstance<TypeName>().any { it.name == iface.name } }
        }.flatten().distinctBy{ it.name }
    }

    private fun getAllObjectTypeMembersOfDiscoveredUnions(): List<ObjectTypeDefinition> {
        val unionTypeNames = dictionary.keys.filterIsInstance<UnionTypeDefinition>().map { union -> union.name }.toSet()
        return dictionary.keys.filterIsInstance<UnionTypeDefinition>().map { union ->
            union.memberTypes.filterIsInstance<TypeName>().filter { !unionTypeNames.contains(it.name) }.map { objectDefinitionsByName[it.name] ?: throw SchemaClassScannerError("No object type found with name '${it.name}' for union: $union") }
        }.flatten().distinct()
    }

    private fun handleInterfaceOrUnionSubTypes(types: List<ObjectTypeDefinition>, failureMessage: (ObjectTypeDefinition) -> String) {
        types.forEach { type ->
            val dictionaryContainsType = dictionary.filter{ it.key.name == type.name }.isNotEmpty()
            if(!unvalidatedTypes.contains(type) && !dictionaryContainsType) {
                val initialEntry = initialDictionary[type.name] ?: throw SchemaClassScannerError(failureMessage(type))
                handleFoundType(type, initialEntry.get(), DictionaryReference())
            }
        }
    }

    /**
     * Scan a new object for types that haven't been mapped yet.
     */
    private fun scanQueueItemForPotentialMatches(item: QueueItem) {
        scanResolverInfoForPotentialMatches(item.type, resolverInfosByDataClass[item.clazz] ?: DataClassResolverInfo(item.clazz))
    }

    private fun scanResolverInfoForPotentialMatches(type: ObjectTypeDefinition, resolverInfo: ResolverInfo) {
        type.getExtendedFieldDefinitions(extensionDefinitions).forEach { field ->
            val fieldResolver = fieldResolverScanner.findFieldResolver(field, resolverInfo)

            fieldResolversByType.getOrPut(type, { mutableMapOf() })[fieldResolver.field] = fieldResolver
            fieldResolver.scanForMatches().forEach { potentialMatch ->
                handleFoundType(typeClassMatcher.match(potentialMatch))
            }
        }
    }

    private fun handleFoundType(match: TypeClassMatcher.Match) {
        when(match) {
            is TypeClassMatcher.ScalarMatch -> {
                handleFoundScalarType(match.type)
            }

            is TypeClassMatcher.ValidMatch -> {
                handleFoundType(match.type, match.clazz, match.reference)
            }
        }
    }

    private fun handleFoundScalarType(type: ScalarTypeDefinition) {
        unvalidatedTypes.add(type)
    }

    /**
     * Enter a found type into the dictionary if it doesn't exist yet, add a reference pointing back to where it was discovered.
     */
    private fun handleFoundType(type: TypeDefinition, clazz: Class<*>?, reference: Reference) {
        val realEntry = dictionary.getOrPut(type, { DictionaryEntry() })
        var typeWasSet = false

        if (clazz != null) {
            typeWasSet = realEntry.setTypeIfMissing(clazz)

            if (realEntry.typeClass != clazz) {
                throw SchemaClassScannerError("Two different classes used for type ${type.name}:\n${realEntry.joinReferences()}\n\n- $clazz:\n|   ${reference.getDescription()}")
            }
        }

        realEntry.addReference(reference)

        // Check if we just added the entry... a little odd, but it works (and thread-safe, FWIW)
        if (typeWasSet && clazz != null) {
            handleNewType(type, clazz)
        }
    }

    /**
     * Handle a newly found type, adding it to the list of actually used types and putting it in the scanning queue if it's an object type.
     */
    private fun handleNewType(graphQLType: TypeDefinition, javaType: Class<*>) {
        when(graphQLType) {
            is ObjectTypeDefinition -> {
                enqueue(graphQLType, javaType)
                scanInterfacesOfType(graphQLType)
            }

            is InputObjectTypeDefinition -> {
                graphQLType.inputValueDefinitions.forEach { inputValueDefinition ->
                    findInputValueType(inputValueDefinition.name, javaType)?.let { inputValueJavaType ->
                        val inputGraphQLType = inputValueDefinition.type.unwrap()
                        if(inputGraphQLType is TypeName && !ScalarInfo.STANDARD_SCALAR_DEFINITIONS.containsKey(inputGraphQLType.name)) {
                            handleFoundType(typeClassMatcher.match(TypeClassMatcher.PotentialMatch.parameterType(
                                inputValueDefinition.type,
                                inputValueJavaType,
                                GenericType(javaType, options).relativeToType(inputValueJavaType),
                                InputObjectReference(inputValueDefinition),
                                false
                            )))
                        }
                    }
                }
            }
        }
    }

    private fun scanInterfacesOfType(graphQLType: ObjectTypeDefinition) {
        graphQLType.implements.forEach {
            if(it is TypeName) {
                handleFoundType(interfaceDefinitionsByName[it.name] ?: throw SchemaClassScannerError("Object type ${graphQLType.name} declared interface ${it.name}, but no interface with that name was found in the schema!"), null, InterfaceReference(graphQLType))
            }
        }
    }

    private fun enqueue(graphQLType: ObjectTypeDefinition, javaType: Class<*>) {
        queue.add(QueueItem(graphQLType, javaType))
    }

    private fun findInputValueType(name: String, clazz: Class<*>): JavaType? {
        val methods = clazz.methods

        return (methods.find {
            it.name == name
        } ?: methods.find {
            it.name == "get${name.capitalize()}"
        })?.genericReturnType ?: clazz.fields.find {
            it.name == name
        }?.genericType
    }

    private data class QueueItem(val type: ObjectTypeDefinition, val clazz: Class<*>)

    private class DictionaryEntry {
        private val references = mutableListOf<Reference>()
        var typeClass: Class<*>? = null
            private set

        fun setTypeIfMissing(typeClass: Class<*>): Boolean {
            if(this.typeClass == null) {
                this.typeClass = typeClass
                return true
            }

            return false
        }

        fun addReference(reference: Reference) {
            references.add(reference)
        }

        fun joinReferences() = "- $typeClass:\n|   " + references.joinToString("\n|   ") { it.getDescription() }
    }

    abstract class Reference {
        abstract fun getDescription(): String
        override fun toString() = getDescription()
    }

    private class DictionaryReference: Reference() {
        override fun getDescription() = "provided dictionary"
    }

    private class InterfaceReference(private val type: ObjectTypeDefinition): Reference() {
        override fun getDescription() = "interface declarations of ${type.name}"
    }

    private class InputObjectReference(private val type: InputValueDefinition): Reference() {
        override fun getDescription() = "input object $type"
    }

    private class InitialDictionaryEntry(private val clazz: Class<*>) {
        var accessed = false
            private set

        fun get(): Class<*> {
            accessed = true
            return clazz
        }
    }

    class ReturnValueReference(private val method: Method): Reference() {
        override fun getDescription() = "return type of method $method"
    }

    class MethodParameterReference(private val method: Method, private val index: Int): Reference() {
        override fun getDescription() = "parameter $index of method $method"
    }

    class FieldTypeReference(private val field: Field): Reference() {
        override fun getDescription() = "type of field $field"
    }

    class RootTypesHolder(options: SchemaParserOptions, rootInfo: RootTypeInfo, definitionsByName: Map<String, TypeDefinition>, queryResolvers: List<GraphQLQueryResolver>, mutationResolvers: List<GraphQLMutationResolver>, subscriptionResolvers: List<GraphQLSubscriptionResolver>) {
        private val queryName = rootInfo.getQueryName()
        private val mutationName = rootInfo.getMutationName()
        private val subscriptionName = rootInfo.getSubscriptionName()

        private val queryDefinition = definitionsByName[queryName]
        private val mutationDefinition = definitionsByName[mutationName]
        private val subscriptionDefinition = definitionsByName[subscriptionName]

        private val queryResolverInfo = RootResolverInfo(queryResolvers, options)
        private val mutationResolverInfo = RootResolverInfo(mutationResolvers, options)
        private val subscriptionResolverInfo = RootResolverInfo(subscriptionResolvers, options)

        val query = createRootType("query", queryDefinition, queryName, true, queryResolvers, GraphQLQueryResolver::class.java, queryResolverInfo)
        val mutation = createRootType("mutation", mutationDefinition, mutationName, rootInfo.isMutationRequired(), mutationResolvers, GraphQLMutationResolver::class.java, mutationResolverInfo)
        val subscription = createRootType("subscription", subscriptionDefinition, subscriptionName, rootInfo.isSubscriptionRequired(), subscriptionResolvers, GraphQLSubscriptionResolver::class.java, subscriptionResolverInfo)

        private fun createRootType(name: String, type: TypeDefinition?, typeName: String, required: Boolean, resolvers: List<GraphQLRootResolver>, resolverInterface: Class<*>, resolverInfo: RootResolverInfo): RootType? {
            if(type == null) {
                if(required) {
                    throw SchemaClassScannerError("Type definition for root $name type '$typeName' not found!")
                }

                return null
            }

            if(type !is ObjectTypeDefinition) {
                throw SchemaClassScannerError("Expected root query type's type to be ${ObjectTypeDefinition::class.java.simpleName}, but it was ${type.javaClass.simpleName}")
            }

            // Find query resolver class
            if(resolvers.isEmpty()) {
                throw SchemaClassScannerError("No Root resolvers for $name type '$typeName' found!  Provide one or more ${resolverInterface.name} to the builder.")
            }

            return RootType(name, type, resolvers, resolverInterface, resolverInfo)
        }
    }

    class RootType(val name: String, val type: ObjectTypeDefinition, val resolvers: List<GraphQLRootResolver>, val resolverInterface: Class<*>, val resolverInfo: RootResolverInfo)
}

class SchemaClassScannerError(message: String, throwable: Throwable? = null) : RuntimeException(message, throwable)
