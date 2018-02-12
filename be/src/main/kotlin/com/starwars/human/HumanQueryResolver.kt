package com.starwars.human

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.framework.graphql.TraversalLoader
import com.framework.graphql.fetchOptional
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class HumanQueryResolver(
        val loader: TraversalLoader
) : GraphQLQueryResolver {

    fun human(name: String, environment: DataFetchingEnvironment) =
            loader.fetchOptional<Human> { it.V().has("name", name) }
}
