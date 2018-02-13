package com.starwars.droid

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.framework.graphql.Mutation
import com.framework.gremlin.fetchMany
import com.framework.gremlin.insert
import com.framework.gremlin.mutate
import com.framework.gremlin.vertexIds
import com.starwars.character.setAppearsIn
import com.starwars.character.setFriends
import com.syncleus.ferma.FramedGraph
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class DroidMutationResolver(
        val graph: FramedGraph
) : GraphQLMutationResolver {

    fun createDroid(
            name: String,
            primaryFunction: String,
            friendIds: Set<Long>,
            appearsInIds: Set<Long>,
            environment: DataFetchingEnvironment) = graph.mutate(object : Mutation<Droid>() {

        override fun checkPermissions(graph: FramedGraph) = true

        override fun run(graph: FramedGraph): Droid {
            val droid = graph.insert<Droid>()
            droid.setName(name)
            droid.setPrimaryFunction(primaryFunction)
            droid.setAppearsIn(graph.fetchMany { it.vertexIds(appearsInIds) })
            droid.setFriends(graph.fetchMany { it.vertexIds(friendIds) })
            return droid
        }
    })
}
