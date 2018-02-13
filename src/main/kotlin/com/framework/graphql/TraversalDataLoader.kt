package com.framework.graphql

import com.framework.gremlin.asGremlin
import com.framework.datamodel.edges.Traversal
import com.syncleus.ferma.FramedGraph
import com.syncleus.ferma.Traversable
import com.syncleus.ferma.VertexFrame
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.future.future
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture


@Component
class TraversalLoader(
        graph: FramedGraph,
        options: DataLoaderOptions
): DataLoader<(GraphTraversalSource) -> GraphTraversal<*, *>, List<VertexFrame>>({ keys -> future {
    keys.map { key -> async {
        graph.traverse<Traversable<*, *>> { key(it) }.toList(VertexFrame::class.java)
    } }.map {
        it.await()
    }
} }, options) {

    fun <F: VertexFrame, T: VertexFrame> load(key: Traversal.Bound<F, T>): CompletableFuture<List<VertexFrame>> =
            load { key.asGremlin(it) }
}

inline fun <reified T: VertexFrame> TraversalLoader.fetchOptional(
        noinline traversal: (GraphTraversalSource) -> GraphTraversal<*, *>
): CompletableFuture<T?> =
        load(traversal).thenApplyAsync {
            it.filterIsInstance(T::class.java).optional()
        }

inline fun <reified T: VertexFrame> TraversalLoader.fetchSingle(
        noinline traversal: (GraphTraversalSource) -> GraphTraversal<*, *>
): CompletableFuture<T> =
        load(traversal).thenApplyAsync {
            it.filterIsInstance(T::class.java).single()
        }

inline fun <reified T: VertexFrame> TraversalLoader.fetchMany(
        noinline traversal: (GraphTraversalSource) -> GraphTraversal<*, *>
): CompletableFuture<List<T>> =
        load(traversal).thenApply {
            it.filterIsInstance(T::class.java)
        }

inline fun <F: VertexFrame, reified T: VertexFrame> TraversalLoader.fetch(traversal: Traversal.BoundToOptional<F, T>): CompletableFuture<T?> =
        this.load(traversal).thenApplyAsync {
            it.filterIsInstance(T::class.java).optional()
        }

inline fun <F: VertexFrame, reified T: VertexFrame> TraversalLoader.fetch(traversal: Traversal.BoundToSingle<F, T>): CompletableFuture<T> =
        this.load(traversal).thenApplyAsync {
            it.filterIsInstance(T::class.java).single()
        }

inline fun <F: VertexFrame, reified T: VertexFrame> TraversalLoader.fetch(traversal: Traversal.BoundToMany<F, out T>): CompletableFuture<List<T>> =
        this.load(traversal).thenApplyAsync {
            it.filterIsInstance(T::class.java)
        }

fun <T> Iterable<T>.optional(): T? =
        if (!iterator().hasNext()) null else single()
