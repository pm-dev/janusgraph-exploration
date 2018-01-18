package com.recall.be.graphql.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.recall.be.graphql.dataloaders.StringDataLoader
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class Query(val stringDataLoader: StringDataLoader): GraphQLQueryResolver {

    fun helloWorld(environment: DataFetchingEnvironment) =
            stringDataLoader.load(1)
}
