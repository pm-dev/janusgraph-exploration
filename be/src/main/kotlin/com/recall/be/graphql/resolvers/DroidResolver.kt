package com.recall.be.graphql.resolvers

import com.coxautodev.graphql.tools.GraphQLResolver
import com.recall.be.datamodel.Character
import com.recall.be.datamodel.Droid
import com.recall.be.gremlin.TraversalLoader
import com.recall.be.gremlin.fetch
import com.recall.be.gremlin.fetchMany
import com.recall.be.gremlin.toGremlin
import com.recall.be.relationships.to
import org.springframework.stereotype.Component

@Component
class DroidResolver(
        val loader: TraversalLoader
): GraphQLResolver<Droid> {
    fun getId(droid: Droid) = droid.getId<Long>().toString()
    fun getName(droid: Droid) = droid.getName()
    fun getAppearsIn(droid: Droid) = loader.fetch(droid.appearsIn)
    fun getFriends(droid: Droid) = loader.fetch(droid.friends)

    fun getSecondDegreeFriends(droid: Droid, limit: Int?) = loader.fetchMany<Character> {
        val secondDegree = droid.secondDegreeFriends.toGremlin(it)
                .dedup()
                .filter { it.get().id() != droid.getId() }
        if (limit == null) secondDegree else secondDegree.limit(limit.toLong())
    }
}
