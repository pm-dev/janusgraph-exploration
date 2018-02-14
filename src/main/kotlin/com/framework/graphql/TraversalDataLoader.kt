package com.framework.graphql

import com.framework.datamodel.edges.Traversal
import com.framework.gremlin.asGremlin
import com.syncleus.ferma.FramedGraph
import com.syncleus.ferma.Traversable
import com.syncleus.ferma.VertexFrame
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.future.await
import kotlinx.coroutines.experimental.future.future
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

//@Component
//class TraversalLoader(
//        graph: FramedGraph
//): DataLoader<TraversalProvider, List<VertexFrame>>({ keys -> future {
//    keys.map { key -> async {
//        graph.traverse<Traversable<*, *>> { key(it) }.toList(VertexFrame::class.java)
//    } }.map {
//        it.await()
//    }
//} }, DataLoaderOptions()) {
//
//    fun load(key: (GraphTraversalSource) -> GraphTraversal<*, *>): CompletableFuture<List<VertexFrame>> =
//            load(FunctionTraversalProvider(key))
//
//    fun <F: VertexFrame> load(key: Traversal.Bound<F, *>): CompletableFuture<List<VertexFrame>> =
//            load(BoundTraversalProvider(key))
//}

@Component
class TraversalLoader(
        private val graph: FramedGraph
) {
    private fun load(traversalProvider: TraversalProvider): CompletableFuture<List<VertexFrame>> {
        return future { async {
            graph.traverse<Traversable<*, *>> { traversalProvider(it) }.toList(VertexFrame::class.java)
        }.await() }
    }

    fun load(key: (GraphTraversalSource) -> GraphTraversal<*, *>): CompletableFuture<List<VertexFrame>> =
            load(FunctionTraversalProvider(key))

    fun <F: VertexFrame> load(key: Traversal.Bound<F, *>): CompletableFuture<List<VertexFrame>> =
            load(BoundTraversalProvider(key))
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
        load(traversal).thenApplyAsync {
            it.filterIsInstance(T::class.java)
        }

inline fun <F: VertexFrame, reified T: VertexFrame> TraversalLoader.fetch(
        traversal: Traversal.BoundToOptional<F, out T>
): CompletableFuture<T?> =
        load(traversal).thenApplyAsync {
            it.filterIsInstance(T::class.java).optional()
        }

inline fun <F: VertexFrame, reified T: VertexFrame> TraversalLoader.fetch(
        traversal: Traversal.BoundToSingle<F, out T>
): CompletableFuture<T> =
        load(traversal).thenApplyAsync {
            it.filterIsInstance(T::class.java).single()
        }

inline fun <F: VertexFrame, reified T: VertexFrame> TraversalLoader.fetch(
        traversal: Traversal.BoundToMany<F, out T>
): CompletableFuture<List<T>> =
        load(traversal).thenApplyAsync {
            it.filterIsInstance(T::class.java)
        }

fun <T> Iterable<T>.optional(): T? =
        if (!iterator().hasNext()) null else single()

// Implementing the following as data classes enables a data loader cache hit on the same traversal

interface TraversalProvider {
    operator fun invoke(source: GraphTraversalSource): GraphTraversal<*, *>
}

data class BoundTraversalProvider<F: VertexFrame>(private val traversal: Traversal.Bound<F, *>): TraversalProvider {
    override fun invoke(source: GraphTraversalSource): GraphTraversal<*, *> = traversal.asGremlin(source)
}

data class FunctionTraversalProvider(private val traversal: (GraphTraversalSource) -> GraphTraversal<*, *>): TraversalProvider {
    override fun invoke(source: GraphTraversalSource) = traversal(source)
}
