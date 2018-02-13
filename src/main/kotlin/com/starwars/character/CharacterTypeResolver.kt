package com.starwars.character

import com.framework.datamodel.node.NodeTypeResolver
import com.framework.graphql.TraversalLoader
import com.framework.graphql.fetch
import com.framework.graphql.fetchMany
import com.framework.gremlin.asGremlin

interface CharacterTypeResolver: NodeTypeResolver {

    val loader: TraversalLoader

    fun getName(character: Character) = character.getName()
    fun getAppearsIn(character: Character) = loader.fetch(character.toAppearsIn)
    fun getFriends(character: Character) = loader.fetch(character.toFriends)

    fun getSecondDegreeFriends(character: Character, limit: Int?) = loader.fetchMany<Character> {
        val secondDegree = character.toSecondDegreeFriends.asGremlin(it)
                .dedup()
                .filter { it.get().id() != character.getId() }
        if (limit == null) secondDegree else secondDegree.limit(limit.toLong())
    }
}
