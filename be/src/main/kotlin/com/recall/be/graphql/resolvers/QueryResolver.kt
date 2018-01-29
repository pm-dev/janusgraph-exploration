package com.recall.be.graphql.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.recall.be.datamodel.Character
import com.recall.be.datamodel.Droid
import com.recall.be.datamodel.Human
import com.recall.be.gremlin.TraversalLoader
import com.recall.be.gremlin.fetchOptional
import com.recall.be.gremlin.fetchSingle
import com.syncleus.ferma.VertexFrame
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component


@Component
class QueryResolver(
        val loader: TraversalLoader
): GraphQLQueryResolver {

    fun node(id: String, environment: DataFetchingEnvironment) =
            loader.fetchOptional<VertexFrame> { it.V(id.toLong()) }

    fun hero(environment: DataFetchingEnvironment) =
            loader.fetchSingle<Character> { it.V().has("name", "Luke Skywalker") }

    fun human(name: String, environment: DataFetchingEnvironment) =
            loader.fetchOptional<Human> { it.V().has("name", name) }

    fun droid(name: String, environment: DataFetchingEnvironment) =
            loader.fetchOptional<Droid> { it.V().has("name", name) }

    fun character(name: String, environment: DataFetchingEnvironment) =
            loader.fetchOptional<Character> {  it.V().has("name", name) }
}
