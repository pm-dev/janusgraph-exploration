package com.recall.be.graphql.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.recall.be.graphql.dataloaders.VertexDataLoader
import graphql.schema.DataFetchingEnvironment
import org.janusgraph.core.JanusGraph
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class Query(
        val graph: JanusGraph,
        val dataLoader: VertexDataLoader): GraphQLQueryResolver {

    fun helloWorld(environment: DataFetchingEnvironment): CompletableFuture<List<String>> {
        val g = graph.traversal()
        val saturn = g.V().has("name", "saturn").next()
        val hercules = g.V().has("name", "hercules").next()
        return dataLoader.loadMany(listOf(saturn, hercules))
    }
}
