package com.starwars.droid

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.framework.graphql.TraversalLoader
import com.framework.graphql.fetchOptional
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component


@Component
class DroidQueryResolver(
        val loader: TraversalLoader
): GraphQLQueryResolver {

    fun droid(name: String, environment: DataFetchingEnvironment) =
            loader.fetchOptional<Droid> { it.V().has("name", name) }
}
