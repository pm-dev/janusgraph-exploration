package com.recall.be.graphql.dataloaders

import com.recall.be.datamodel.Character
import com.recall.be.datamodel.Droid
import com.recall.be.datamodel.Human
import com.syncleus.ferma.Traversable
import com.syncleus.ferma.VertexFrame
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.future.future
import org.dataloader.BatchLoader
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import org.springframework.stereotype.Component
import java.util.concurrent.CompletionStage
import kotlin.reflect.KClass


inline fun <reified T: VertexFrame> EntityLoader.loadMaybe(traversal: Traversable<VertexFrame, T>) =
        T::class.loader().load(traversal).thenApplyAsync { it.maybe() }

inline fun <reified T: VertexFrame> EntityLoader.loadSingle(traversal: Traversable<VertexFrame, T>) =
        T::class.loader().load(traversal).thenApplyAsync { it.single() }

inline fun <reified T: VertexFrame> EntityLoader.loadMany(traversal: Traversable<VertexFrame, T>) =
        T::class.loader().load(traversal)

@Component
class EntityLoader(
        val characterDataLoader: CharacterDataLoader,
        val humanDataLoader: HumanDataLoader,
        val droidDataLoader: DroidDataLoader
) {
    final inline fun <reified T: VertexFrame> KClass<T>.loader() = when (this) {
        Human::class -> humanDataLoader
        Character::class -> characterDataLoader
        Droid::class -> droidDataLoader
        else -> throw IllegalStateException()
    } as DataLoader<Traversable<VertexFrame, T>, List<T>>
}

@Component
class CharacterDataLoader(
        options: DataLoaderOptions
): DataLoader<Traversable<VertexFrame, Character>, List<Character>>(BatchTraversalLoader(Character::class), options)

@Component
class HumanDataLoader(
        options: DataLoaderOptions
): DataLoader<Traversable<VertexFrame, Human>, List<Human>>(BatchTraversalLoader(Human::class), options)

@Component
class DroidDataLoader(
        options: DataLoaderOptions
): DataLoader<Traversable<VertexFrame, Human>, List<Human>>(BatchTraversalLoader(Human::class), options)

private class BatchTraversalLoader<K: VertexFrame>(
        private val clazz: KClass<K>
): BatchLoader<Traversable<VertexFrame, K>, List<K>> {
    override fun load(keys: MutableList<Traversable<VertexFrame, K>>?): CompletionStage<List<List<K>>> {
        return future {
            keys
                    ?.map { async { it.toList(clazz.java) } }
                    ?.map { it.await() }
                    ?: listOf<List<K>>()
        }
    }
}

fun <T> Iterable<T>.maybe(): T? =
        if (!iterator().hasNext()) null else single()
