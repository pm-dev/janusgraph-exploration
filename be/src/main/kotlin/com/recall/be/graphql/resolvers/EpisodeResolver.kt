package com.recall.be.graphql.resolvers

import com.coxautodev.graphql.tools.GraphQLResolver
import com.recall.be.datamodel.Episode
import com.recall.be.gremlin.TraversalLoader
import org.springframework.stereotype.Component

@Component
class EpisodeResolver(
        val loader: TraversalLoader
): GraphQLResolver<Episode> {
    fun getId(episode: Episode) = episode.getId<Long>().toString()
    fun getName(episode: Episode) = episode.getName()
}
