package com.recall.be.graphql.dataloaders

import com.recall.be.datamodel.asGod
import com.recall.be.datamodel.asTitan
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.future.future
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import org.janusgraph.core.JanusGraph
import org.springframework.stereotype.Component


@Component
class VertexDataLoader(
        graph: JanusGraph,
        options: DataLoaderOptions
): DataLoader<(GraphTraversalSource) -> GraphTraversal<Vertex, Vertex>, List<Vertex>>({ keys ->
    future {
        val source = graph.traversal()
        keys
                .map { it(source) }
                .map { async {
                    println("sleeping for a second")
                    Thread.sleep(1 * 1000)
                    it.toList()
                } }
                .map { it.await() }
    }
}, options)


fun VertexDataLoader.loadTitan(traversal: (GraphTraversalSource) -> GraphTraversal<Vertex, Vertex>) =
        load { traversal(it) }.thenApplyAsync { it.single().asTitan() }

fun VertexDataLoader.loadTitanMaybe(traversal: (GraphTraversalSource) -> GraphTraversal<Vertex, Vertex>) =
        load { traversal(it) }.thenApplyAsync { it.maybe()?.asTitan() }

fun VertexDataLoader.loadTitans(traversal: (GraphTraversalSource) -> GraphTraversal<Vertex, Vertex>) =
        load { traversal(it) }.thenApplyAsync { it.map { it.asTitan() } }

fun VertexDataLoader.loadGod(traversal: (GraphTraversalSource) -> GraphTraversal<Vertex, Vertex>) =
        load { traversal(it) }.thenApplyAsync { it.single().asGod() }

fun VertexDataLoader.loadGodMaybe(traversal: (GraphTraversalSource) -> GraphTraversal<Vertex, Vertex>) =
        load { traversal(it) }.thenApplyAsync { it.maybe()?.asGod() }

fun VertexDataLoader.loadGods(traversal: (GraphTraversalSource) -> GraphTraversal<Vertex, Vertex>) =
        load { traversal(it) }.thenApplyAsync { it.map { it.asGod() } }

fun <T> Iterable<T>.maybe(): T? =
        if (!iterator().hasNext()) null else single()
