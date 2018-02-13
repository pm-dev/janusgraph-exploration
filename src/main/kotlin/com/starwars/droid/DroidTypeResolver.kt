package com.starwars.droid

import com.coxautodev.graphql.tools.GraphQLResolver
import com.framework.graphql.TraversalLoader
import com.starwars.character.CharacterTypeResolver
import org.springframework.stereotype.Component

@Component
class DroidTypeResolver(
        override val loader: TraversalLoader
): CharacterTypeResolver, GraphQLResolver<Droid> {

    fun getPrimaryFunction(droid: Droid) = droid.getPrimaryFunction()

    // These redundant overrides are necessary for graphql.tools
    fun getId(node: Droid) = super.getId(node)
    fun getName(character: Droid) = super.getName(character)
    fun getAppearsIn(character: Droid) = super.getAppearsIn(character)
    fun getFriends(character: Droid) = super.getFriends(character)
    fun getSecondDegreeFriends(character: Droid, limit: Int?) = super.getSecondDegreeFriends(character, limit)
}
