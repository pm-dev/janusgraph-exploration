package com.starwars.character

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.framework.graphql.TraversalLoader
import com.framework.graphql.fetchOptional
import com.framework.graphql.fetchSingle
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class CharacterQueryResolver(
        val loader: TraversalLoader
): GraphQLQueryResolver {

    fun hero(environment: DataFetchingEnvironment) =
            loader.fetchSingle<Character> { it.V().has("name", "Luke Skywalker") }

    fun character(name: String, environment: DataFetchingEnvironment) =
            loader.fetchOptional<Character> {  it.V().has("name", name) }
}
