package com.recall.be.graphql.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.coxautodev.graphql.tools.GraphQLResolver
import com.recall.be.datamodel.*
import com.recall.be.graphql.dataloaders.TraversableLoader
import com.recall.be.graphql.dataloaders.fetch
import com.syncleus.ferma.DelegatingFramedGraph
import graphql.schema.DataFetchingEnvironment
import org.janusgraph.core.JanusGraph
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture


@Component
class Query(
        val graph: DelegatingFramedGraph<JanusGraph>,
        val loader: TraversableLoader
): GraphQLQueryResolver {

    fun node(id: String, environment: DataFetchingEnvironment) =
            loader.fetch(graph.nodes.hasId(id.toLong()))

    fun hero(environment: DataFetchingEnvironment): CompletableFuture<Character> =
            loader.fetch(graph.characters.has(property = "name", value = "Luke Skywalker").toSingle())

//    fun hero(episode: String?, environment: DataFetchingEnvironment) =
//            loader.fetch(graph.characters.has(property = "name", value = "Luke Skywalker").asSingle)

    fun human(name: String, environment: DataFetchingEnvironment) =
            loader.fetch(graph.humans.has(property = "name", value = name).toOptional())

    fun droid(name: String, environment: DataFetchingEnvironment) =
            loader.fetch(graph.droids.has(property = "name", value = name).toOptional())

    fun character(name: String, environment: DataFetchingEnvironment) =
            loader.fetch(graph.characters.has(property = "name", value = name).toOptional())
}

@Component
class HumanResolver(
        val loader: TraversableLoader
): GraphQLResolver<Human> {
    fun getId(human: Human) = human.getId<Long>().toString()
    fun getName(human: Human) = human.getName()
    fun getAppearsIn(human: Human) =
            loader.fetch(human.then.toAppearsIn)
    fun getFriends(human: Human) =
            loader.fetch(human.then.toFriends)
}

@Component
class DroidResolver(
        val loader: TraversableLoader
): GraphQLResolver<Droid> {
    fun getId(droid: Droid) = droid.getId<Long>().toString()
    fun getName(droid: Droid) = droid.getName()
    fun getAppearsIn(droid: Droid) =
            loader.fetch(droid.then.toAppearsIn)
    fun getFriends(droid: Droid) =
            loader.fetch(droid.then.toFriends)
}

@Component
class EpisodeResolver(
        val loader: TraversableLoader
): GraphQLResolver<Episode> {
    fun getName(episode: Episode) = episode.getName()
}
