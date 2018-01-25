package com.recall.be.datamodel

import com.syncleus.ferma.VertexFrame
import com.syncleus.ferma.annotations.Adjacency
import com.syncleus.ferma.annotations.GraphElement
import com.syncleus.ferma.annotations.Incidence
import com.syncleus.ferma.annotations.Property
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.DefaultGraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.step.map.VertexStep
import org.apache.tinkerpop.gremlin.structure.Direction
import org.apache.tinkerpop.gremlin.structure.Vertex

@GraphElement
interface Character: Vertex {

    val id get() = "123"

    @Property("name")
    fun getName(): String

    @Property("name")
    fun setName(name: String)

    @Adjacency(label = "appearsIn")
    fun getAppearsIn(): List<Episode>

    @Incidence(label = "appearsIn")
    fun addAppearsIn(appearsIn: Episode)

//    @Adjacency(label = "friends")
    fun getFriends(): List<Character> {
        toFriends.ne
    }

    @Incidence(label = "friends")
    fun addFriend(friend: Character)

    fun friendsNamedLuke(): List<Character> {
        return traverse<> { input -> input.out("friends").has("name", "Luke") }.toList(Character::class.java)
    }

    fun addFriends(vararg friends: Character) =
            friends.forEach { addFriend(it) }

    fun addAppearsIns(vararg episodes: Episode) =
            episodes.forEach { addAppearsIn(it) }

    companion object {
        val toFriends = VertexStep<Character>(
                DefaultGraphTraversal<Character, Character>(),
                Character::class.java,
                Direction.OUT,
                "friends")
    }
}
