package com.starwars.character

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.syncleus.ferma.FramedGraph
import org.springframework.stereotype.Component

@Component
class CharacterMutationResolver(
        val graph: FramedGraph
): GraphQLMutationResolver
