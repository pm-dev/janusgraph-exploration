package com.starwars.episode

import com.coxautodev.graphql.tools.GraphQLResolver
import com.framework.datamodel.node.Node
import com.framework.datamodel.node.NodeTypeResolver
import com.framework.graphql.TraversalLoader
import com.starwars.droid.Droid
import com.starwars.human.Human
import org.springframework.stereotype.Component

@Component
class EpisodeTypeResolver: NodeTypeResolver, GraphQLResolver<Episode> {
    fun getName(episode: Episode) = episode.getName()

    // This redundant override is necessary for graphql.tools
    fun getId(node: Episode) = super.getId(node)
}
