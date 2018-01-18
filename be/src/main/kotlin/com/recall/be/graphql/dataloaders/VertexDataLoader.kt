package com.recall.be.graphql.dataloaders

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import org.janusgraph.core.JanusGraph
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors


@Component
class VertexDataLoader(
        graph: JanusGraph,
        options: DataLoaderOptions
): DataLoader<(GraphTraversalSource) -> GraphTraversal<Vertex, Vertex>, List<Vertex>>({ keys ->
    CompletableFuture.supplyAsync({
        val source = graph.traversal()
        keys.parallelStream().map { it(source).toList()
        }.collect(Collectors.toList())
    })
}, options)

