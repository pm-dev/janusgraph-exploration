package com.recall.be.graphql.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.recall.be.datamodel.*
import com.recall.be.graphql.dataloaders.VertexDataLoader
import graphql.schema.DataFetchingEnvironment
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.janusgraph.core.JanusGraph
import org.reflections.ReflectionUtils.withName
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class Query(
        val graph: JanusGraph,
        val dataLoader: VertexDataLoader): GraphQLQueryResolver {

    fun helloWorld(environment: DataFetchingEnvironment): Titan {
        val one = graph.traversal().V().has("name", "saturn").next().asTitan()
        return one
    }
}
