package com.recall.be.datamodel

import com.syncleus.ferma.VertexFrame
import com.syncleus.ferma.annotations.Adjacency
import com.syncleus.ferma.annotations.GraphElement
import com.syncleus.ferma.annotations.Incidence
import com.syncleus.ferma.annotations.Property

@GraphElement
interface Character: VertexFrame {

    val id get() = "123"

    @Property("name")
    fun getName(): String

    @Property("name")
    fun setName(name: String)

    @Adjacency(label = "appearsIn")
    fun getAppearsIn(): List<Episode>

    @Incidence(label = "appearsIn")
    fun addAppearsIn(appearsIn: Episode)

    @Adjacency(label = "friends")
    fun getFriends(): List<Character>

    @Incidence(label = "friends")
    fun addFriend(friend: Character)

    fun addFriends(vararg friends: Character) =
            friends.forEach { addFriend(it) }

    fun addAppearsIns(vararg episodes: Episode) =
            episodes.forEach { addAppearsIn(it) }
}
