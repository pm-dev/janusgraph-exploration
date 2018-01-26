package com.recall.be.datamodel

import com.syncleus.ferma.VertexFrame
import com.syncleus.ferma.annotations.Adjacency
import com.syncleus.ferma.annotations.GraphElement
import com.syncleus.ferma.annotations.Incidence
import com.syncleus.ferma.annotations.Property

@GraphElement
interface Character: VertexFrame {

    @Property("name")
    fun getName(): String

    @Property("name")
    fun setName(name: String)

    @Adjacency(label = "appearsIn")
    fun getAppearsIn(): List<Episode>

    @Incidence(label = "appearsIn")
    fun addAppearsIn(appearsIn: Episode)

    @Adjacency(label = "appearsIn")
    fun setAppearsIn(appearsIn: Set<Episode>)

    @Adjacency(label = "friends")
    fun getFriends(): Set<Character>

    @Incidence(label = "friends")
    fun addFriend(friend: Character)

    @Adjacency(label = "friends")
    fun setFriends(friends: Set<Character>)
}

fun Character.setAppearsIn(vararg appearsIn: Episode) = setAppearsIn(appearsIn.toSet())
fun Character.addAppearsIn(vararg appearsIn: Episode) = appearsIn.forEach { addAppearsIn(it) }
fun Character.addAppearsIn(appearsIn: Set<Episode>) = appearsIn.forEach { addAppearsIn(it) }

fun Character.setFriends(vararg friends: Character) = setFriends(friends.toSet())
fun Character.addFriends(vararg friends: Character) = friends.forEach { addFriend(it) }
fun Character.addFriends(friends: Set<Character>) = friends.forEach { addFriend(it) }
