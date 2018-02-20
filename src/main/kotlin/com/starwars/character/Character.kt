package com.starwars.character


import com.framework.datamodel.edges.Relationship
import com.framework.datamodel.edges.to
import com.framework.datamodel.node.Node
import com.starwars.episode.Episode
import com.syncleus.ferma.annotations.Adjacency
import com.syncleus.ferma.annotations.Incidence
import com.syncleus.ferma.annotations.Property

abstract class Character: Node() {

    @Property("name")
    abstract fun getName(): String

    @Property("name")
    abstract fun setName(name: String)

    val toAppearsIn get() = to(appearsIn)

    val toFriends get() = to(friends)

    val toSecondDegreeFriends get() = to(secondDegreeFriends)

    @Incidence(label = "friends")
    abstract fun addFriend(friend: Character)

    @Adjacency(label = "friends")
    abstract fun setFriends(friends: Set<Character>)

    @Incidence(label = "appearsIn")
    abstract fun addAppearsIn(appearsIn: Episode)

    @Adjacency(label = "appearsIn")
    abstract fun setAppearsIn(appearsIn: Set<Episode>)

    companion object {
        val friends = Relationship.AsymmetricManyToMany<Character, Character>(name = "friends")
        val secondDegreeFriends = friends.to(friends)
        val appearsIn = Relationship.AsymmetricManyToMany<Character, Episode>(name = "appearsIn")
    }
}

fun Character.setAppearsIn(appearsIn: Iterable<Episode>) = setAppearsIn(appearsIn.toSet())
fun Character.setAppearsIn(vararg appearsIn: Episode) = setAppearsIn(appearsIn.toSet())
fun Character.addAppearsIn(vararg appearsIn: Episode) = appearsIn.forEach { addAppearsIn(it) }
fun Character.addAppearsIn(appearsIn: Set<Episode>) = appearsIn.forEach { addAppearsIn(it) }

fun Character.setFriends(friends: Iterable<Character>) = setFriends(friends.toSet())
fun Character.setFriends(vararg friends: Character) = setFriends(friends.toSet())
fun Character.addFriends(vararg friends: Character) = friends.forEach { addFriend(it) }
fun Character.addFriends(friends: Set<Character>) = friends.forEach { addFriend(it) }
