package com.starwars.human

import com.coxautodev.graphql.tools.GraphQLResolver
import com.framework.datamodel.node.Node
import com.framework.graphql.TraversalLoader
import com.framework.graphql.fetch
import com.starwars.character.Character
import com.starwars.character.CharacterTypeResolver
import com.starwars.droid.Droid
import org.springframework.stereotype.Component

@Component
class HumanTypeResolver(
        override val loader: TraversalLoader
): CharacterTypeResolver, GraphQLResolver<Human> {

    fun getHomePlanet(human: Human) = human.getHomePlanet()

    // These redundant overrides are necessary for graphql.tools
    fun getId(node: Human) = super.getId(node)
    fun getName(character: Human) = super.getName(character)
    fun getAppearsIn(character: Human) = super.getAppearsIn(character)
    fun getFriends(character: Human) = super.getFriends(character)
    fun getSecondDegreeFriends(character: Human, limit: Int?) = super.getSecondDegreeFriends(character, limit)
}
