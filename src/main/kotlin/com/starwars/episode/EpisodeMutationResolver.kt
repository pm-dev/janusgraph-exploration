package com.starwars.episode

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.syncleus.ferma.FramedGraph
import org.springframework.stereotype.Component

@Component
class EpisodeMutationResolver(
        val graph: FramedGraph
): GraphQLMutationResolver
