package com.recall.be.graphql.dataloaders

import com.recall.be.gremlin.TraversableToMany
import com.recall.be.gremlin.TraversableToOptional
import com.recall.be.gremlin.TraversableToSingle
import com.recall.be.optional
import com.syncleus.ferma.Traversable
import com.syncleus.ferma.VertexFrame
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.future.future
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture


inline fun <reified T: VertexFrame> TraversableLoader.fetch(traversal: TraversableToOptional<*, T>): CompletableFuture<T?> =
        this.load(traversal).thenApplyAsync {
            it.optional()?.reframe(T::class.java)
        }

inline fun <reified T: VertexFrame> TraversableLoader.fetch(traversal: TraversableToSingle<*, T>): CompletableFuture<T> =
        this.load(traversal).thenApplyAsync {
            it.single().reframe(T::class.java)
        }

inline fun <reified T: VertexFrame> TraversableLoader.fetch(traversal: TraversableToMany<*, out T>): CompletableFuture<List<T>> =
        this.load(traversal).thenApply {
            it.map {
                it.reframe(T::class.java)
            }
        }

@Component
class TraversableLoader(
        options: DataLoaderOptions
): DataLoader<Traversable<*, out VertexFrame>, List<VertexFrame>>({ keys -> future {
    keys.map { async {
        val list = it.toList(VertexFrame::class.java)
        list
    } }.map {
        it.await()
    }
} }, options)
