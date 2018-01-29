package com.recall.be.datamodel

import com.recall.be.relationships.Hop
import com.recall.be.relationships.to
import com.syncleus.ferma.VertexFrame
import com.syncleus.ferma.annotations.Adjacency
import com.syncleus.ferma.annotations.Incidence
import com.syncleus.ferma.annotations.Property

interface Character: VertexFrame {

    @Property("name")
    fun getName(): String

    @Property("name")
    fun setName(name: String)

    val appearsIn get() = to(Character.appearsIn)

    val friends get() = to(Character.friends)

    val secondDegreeFriends get() = to(Character.secondDegreeFriends)

    @Incidence(label = "friends")
    fun addFriend(friend: Character)

    @Adjacency(label = "friends")
    fun setFriends(friends: Set<Character>)

    @Incidence(label = "appearsIn")
    fun addAppearsIn(appearsIn: Episode)

    @Adjacency(label = "appearsIn")
    fun setAppearsIn(appearsIn: Set<Episode>)

    companion object {
        val friends = Hop.AsymmetricManyToMany<Character, Character>(name = "friends")
        val secondDegreeFriends = friends.to(friends)
        val appearsIn = Hop.AsymmetricManyToMany<Character, Episode>(name = "appearsIn")
    }
}

fun Character.setAppearsIn(vararg appearsIn: Episode) = setAppearsIn(appearsIn.toSet())
fun Character.addAppearsIn(vararg appearsIn: Episode) = appearsIn.forEach { addAppearsIn(it) }
fun Character.addAppearsIn(appearsIn: Set<Episode>) = appearsIn.forEach { addAppearsIn(it) }

fun Character.setFriends(vararg friends: Character) = setFriends(friends.toSet())
fun Character.addFriends(vararg friends: Character) = friends.forEach { addFriend(it) }
fun Character.addFriends(friends: Set<Character>) = friends.forEach { addFriend(it) }
