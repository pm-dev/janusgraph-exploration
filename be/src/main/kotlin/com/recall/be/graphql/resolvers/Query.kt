package com.recall.be.graphql.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.coxautodev.graphql.tools.GraphQLResolver
import com.recall.be.datamodel.*
import com.recall.be.graphql.dataloaders.*
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component


@Component
class Query(
        val loader: TraversableLoader
): GraphQLQueryResolver {

    fun hero(episode: String?, environment: DataFetchingEnvironment) =
            loader.loadMaybe<Character> { it.V().has("name", "Luke Skywalker") }

    fun human(name: String, environment: DataFetchingEnvironment) =
            loader.loadMaybe<Human> { it.V().has("name", name) }

    fun droid(name: String, environment: DataFetchingEnvironment) =
            loader.loadMaybe<Droid> { it.V().has("name", name) }

    fun character(name: String, environment: DataFetchingEnvironment) =
            loader.loadMaybe<Character> { it.V().has("name", name) }
}

@Component
class HumanResolver(
        val loader: TraversableLoader
): GraphQLResolver<Human> {
    fun getId(human: Human) = human.getId<Long>().toString()
    fun getName(human: Human) = human.getName()
    fun getAppearsIn(human: Human) =
            loader.loadMany<Episode> { it.from(human).toAppearsIn }
    fun getFriends(human: Human) =
            loader.loadMany<Character> { it.from(human).toFriends }
}

//.out("friends")


@Component
class DroidResolver(
        val loader: TraversableLoader,
        val loader2: TraversableLoader2
): GraphQLResolver<Droid> {
    fun getId(droid: Droid) = droid.getId<Long>().toString()
    fun getName(droid: Droid) = droid.getName()
    fun getAppearsIn(droid: Droid) =
            loader2.loadMany(droid.toAppearsIn)
    fun getFriends(droid: Droid) =
            loader2.loadMany<Character> { it.from(droid).toFriends }
}

@Component
class EpisodeResolver(
        val loader: TraversableLoader
): GraphQLResolver<Episode> {
    fun getName(episode: Episode) = episode.getName()
}
