package com.recall.be.graphql.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.coxautodev.graphql.tools.GraphQLResolver
import com.recall.be.datamodel.*
import com.recall.be.graphql.dataloaders.VertexDataLoader
import com.recall.be.graphql.dataloaders.loadTitan
import com.syncleus.ferma.FramedGraph
import com.syncleus.ferma.Traversable
import com.syncleus.ferma.VertexFrame
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.future.future
import org.dataloader.BatchLoader
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.function.Function

@Component
class Query(
        val fg: FramedGraph,
        val dataLoader: VertexDataLoader
): GraphQLQueryResolver {

    fun helloWorld(environment: DataFetchingEnvironment): CompletableFuture<Titan> = dataLoader.loadTitan { source ->
        Thread.sleep(5 * 1000)
        source.V().has("name", "saturn")
    }

    fun helloWorld2(environment: DataFetchingEnvironment): CompletableFuture<List<Titan>> = dataLoader.loadMany(
            listOf(
                    { source -> source.V().has("name", "saturn") },
                    { source -> source.V().has("name", "saturn") },
                    { source -> source.V().has("name", "saturn") },
                    { source -> source.V().has("name", "saturn") },
                    { source -> source.V().has("name", "saturn") },
                    { source -> source.V().has("name", "saturn") },
                    { source -> source.V().has("name", "saturn") },
                    { source -> source.V().has("name", "saturn") },
                    { source -> source.V().has("name", "saturn") },
                    { source -> source.V().has("name", "saturn") },
                    { source -> source.V().has("name", "saturn") },
                    { source -> source.V().has("name", "saturn") },
                    { source -> source.V().has("name", "saturn") }))
            .thenApply { it.map {  it.single().asTitan() } }


    fun hero(episode: String?, environment: DataFetchingEnvironment): Character? {
        return when (episode) {
            null ->  fg.traverse<Traversable<*, *>> { g -> g.V().has("name", "Luke Skywalker") }.next(Human::class.java)
            else ->  fg.traverse<Traversable<*, *>> { g -> g.V().has("name", "Luke Skywalker") }.next(Human::class.java)
        }
    }

    fun human(name: String, environment: DataFetchingEnvironment): Human? {
        return fg.traverse<Traversable<Human, Human>> { it.V().has("name", name) }.next(Human::class.java)
    }


    fun droid(name: String, environment: DataFetchingEnvironment): Droid? {
        return fg.traverse<Traversable<Droid, Droid>> { it.V().has("name", name) }.next(Droid::class.java)
    }

    fun character(name: String, environment: DataFetchingEnvironment): Character? {
        return fg.traverse<Traversable<Character, Character>> { it.V().has("name", name) }.next(Character::class.java)
    }
}

//
//{ keys ->
//    future {
//        val source = graph.traversal()
//        keys
//                .map { it(source) }
//                .map { async {
//                    println("sleeping for a second")
//                    Thread.sleep(1 * 1000)
//                    it.
//                } }
//                .map { it.await() }
//    }
//}

class BatchTraversalLoader<K: VertexFrame>(
        val clazz: Class<K>
): BatchLoader<Traversable<VertexFrame, K>, List<K>> {

    override fun load(keys: MutableList<Traversable<VertexFrame, K>>?): CompletionStage<List<List<K>>> {
        return future {
            keys
                    ?.map { async { it.toList(clazz) } }
                    ?.map { it.await() }
                    ?: listOf<List<K>>()
        }
    }
}


//@Component
//class HumanResolver: GraphQLResolver<Human> {
//    fun getId(human: Human) = human.id
//    fun getName(human: Human) = human.getName()
//    fun getAppearsIn(human: Human) = human.getAppearsIn()
//    fun getFriends(human: Human) = human.getFriends()
//}
//
//@Component
//class DroidResolver: GraphQLResolver<Droid> {
//    fun getId(droid: Droid) = droid.id
//    fun getName(droid: Droid) = droid.getName()
//    fun getAppearsIn(droid: Droid) = droid.getAppearsIn()
//    fun getFriends(droid: Droid) = droid.getFriends()
//}
//
//@Component
//class EpisodeResolver: GraphQLResolver<Episode> {
//    fun getName(episode: Episode) = episode.getName()
//}
