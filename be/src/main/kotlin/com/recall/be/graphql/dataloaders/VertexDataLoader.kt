package com.recall.be.graphql.dataloaders

import org.apache.tinkerpop.gremlin.structure.Vertex
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import org.janusgraph.core.JanusGraph
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class VertexDataLoader(
        graph: JanusGraph,
        options: DataLoaderOptions): DataLoader<Vertex, String>({ keys ->
    CompletableFuture.supplyAsync({
       graph.traversal().V(keys).valueMap<Any>().next(keys.size).map { it.toString() }
    })
}, options)

