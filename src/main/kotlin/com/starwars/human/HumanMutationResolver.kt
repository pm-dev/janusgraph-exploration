package com.starwars.human

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.syncleus.ferma.FramedGraph
import org.springframework.stereotype.Component

@Component
class HumanMutationResolver(
        val graph: FramedGraph
): GraphQLMutationResolver
