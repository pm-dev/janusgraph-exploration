package com.recall.be.graphql.resolvers

import com.coxautodev.graphql.tools.GraphQLResolver
import com.recall.be.datamodel.Human
import com.recall.be.gremlin.TraversalLoader
import com.recall.be.gremlin.fetch
import org.springframework.stereotype.Component

@Component
class HumanResolver(
        val loader: TraversalLoader
): GraphQLResolver<Human> {
    fun getId(human: Human) = human.getId<Long>().toString()
    fun getName(human: Human) = human.getName()
    fun getAppearsIn(human: Human) = loader.fetch(human.appearsIn)
    fun getFriends(human: Human) = loader.fetch(human.friends)
}
