package com.recall.be.datamodel

import com.recall.be.gremlin.TraversableToMany
import com.recall.be.gremlin.traverseToMany
import com.recall.be.gremlin.verticies
import com.syncleus.ferma.DelegatingFramedGraph
import com.syncleus.ferma.Traversable
import com.syncleus.ferma.annotations.Adjacency
import com.syncleus.ferma.annotations.GraphElement
import com.syncleus.ferma.annotations.Incidence
import com.syncleus.ferma.annotations.Property

@GraphElement
interface Character: Node {

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

val Traversable<out Any?, out Character>.toFriends: TraversableToMany<*, Character> get() =
    traverse { it.out("friends") }

val Traversable<out Any?, out Character>.toAppearsIn: TraversableToMany<*, Episode> get() =
    traverse { it.out("appearsIn") }

val DelegatingFramedGraph<*>.characters: TraversableToMany<Any, Character> get() = traverseToMany { it.verticies() }
