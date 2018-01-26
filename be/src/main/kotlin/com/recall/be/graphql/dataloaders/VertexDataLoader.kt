package com.recall.be.graphql.dataloaders

import com.syncleus.ferma.FramedGraph
import com.syncleus.ferma.Traversable
import com.syncleus.ferma.VertexFrame
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.future.future
import org.apache.tinkerpop.gremlin.process.traversal.Traversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.dataloader.BatchLoader
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage


inline fun <reified T: VertexFrame> TraversableLoader.loadMaybe(traversal: (GraphTraversalSource) -> Traversal<Vertex, Vertex>): CompletableFuture<T?> =
        this.load(traversal(graph.rawTraversal)).thenApplyAsync { it.maybe()?.let { graph.frameElement(it, T::class.java) } }

inline fun <reified T: VertexFrame> TraversableLoader.loadSingle(traversal: (GraphTraversalSource) -> Traversal<Vertex, Vertex>): CompletableFuture<T> =
        this.load(traversal(graph.rawTraversal)).thenApplyAsync { it.single().let { graph.frameElement(it, T::class.java) } }

inline fun <reified T: VertexFrame> TraversableLoader.loadMany(traversal: (GraphTraversalSource) -> Traversal<Vertex, Vertex>): CompletableFuture<List<T>> =
        this.load(traversal(graph.rawTraversal)).thenApply { it.map { graph.frameElement(it, T::class.java) } }


inline fun <reified T: VertexFrame> TraversableLoader2.loadMany(traversal: Traversable<*, T>): CompletableFuture<List<T>> =
        this.load(traversal.rawTraversal).thenApply { it.map { it.reframe(T::class.java) } }

@Component
class TraversableLoader(
        val graph: FramedGraph,
        options: DataLoaderOptions
): DataLoader<Traversal<*, Vertex>, List<Vertex>>(BatchTraversalLoader(), options) {
//    fun s() {
//        graph.traverse<> {  }
//    }
}

@Component
class TraversableLoader2(
        val graph: FramedGraph,
        options: DataLoaderOptions
): DataLoader<Traversal<*, out VertexFrame>, List<VertexFrame>>(BatchTraversalLoader2(), options) {
//    fun s() {
//        graph.traverse<> {  }
//    }
}

private class BatchTraversalLoader: BatchLoader<Traversal<*, Vertex>, List<Vertex>> {
    override fun load(keys: MutableList<Traversal<*, Vertex>>): CompletionStage<List<List<Vertex>>> {
        return future {
            keys.map { async { it.toList() } }.map { it.await() }
        }
    }
}

private class BatchTraversalLoader2: BatchLoader<Traversal<*, out VertexFrame>, List<VertexFrame>> {
    override fun load(keys: MutableList<Traversal<*, out VertexFrame>>): CompletionStage<List<List<VertexFrame>>> {
        return future {
            keys.map { async { it.toList() } }.map { it.await() }
        }
    }
}

fun <T> Iterable<T>.maybe(): T? =
        if (!iterator().hasNext()) null else single()
