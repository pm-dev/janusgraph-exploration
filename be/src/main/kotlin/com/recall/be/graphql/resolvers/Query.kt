package com.recall.be.graphql.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.coxautodev.graphql.tools.GraphQLResolver
import com.recall.be.datamodel.Character
import com.recall.be.datamodel.Droid
import com.recall.be.datamodel.Episode
import com.recall.be.datamodel.Human
import com.recall.be.graphql.dataloaders.EntityLoader
import com.recall.be.graphql.dataloaders.loadMany
import com.recall.be.graphql.dataloaders.loadMaybe
import com.syncleus.ferma.FramedGraph
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class Query(
        val fg: FramedGraph,
        val loader: EntityLoader
): GraphQLQueryResolver {

    fun hero(episode: String?, environment: DataFetchingEnvironment) =
            loader.loadMaybe<Character>(fg.traverse { it.V().has("name", "Luke Skywalker") })

    fun human(name: String, environment: DataFetchingEnvironment) =
            loader.loadMaybe<Human>(fg.traverse { it.V().has("name", name) })

    fun droid(name: String, environment: DataFetchingEnvironment) =
            loader.loadMaybe<Droid>(fg.traverse { it.V().has("name", name) })

    fun character(name: String, environment: DataFetchingEnvironment) =
            loader.loadMaybe<Character>(fg.traverse { it.V().has("name", name) })
}

@Component
class HumanResolver(
        val fg: FramedGraph,
        val loader: EntityLoader
): GraphQLResolver<Human> {
    fun getId(human: Human) = human.getId<Long>().toString()
    fun getName(human: Human) = human.getName()
    fun getAppearsIn(human: Human) = human.getAppearsIn()
    fun getFriends(human: Human) =
            loader.loadMany<Character>(fg.traverse { it.V(human.getId()).out("friends") })
}

@Component
class DroidResolver(
        val fg: FramedGraph,
        val loader: EntityLoader
): GraphQLResolver<Droid> {
    fun getId(droid: Droid) = droid.getId<Long>().toString()
    fun getName(droid: Droid) = droid.getName()
    fun getAppearsIn(droid: Droid) = droid.getAppearsIn()
    fun getFriends(droid: Droid) =
            loader.loadMany<Character>(fg.traverse { it.V(droid.getId()).out("friends") })
}

@Component
class EpisodeResolver(
        val fg: FramedGraph,
        val loader: EntityLoader
): GraphQLResolver<Episode> {
    fun getName(episode: Episode) = episode.getName()
}
