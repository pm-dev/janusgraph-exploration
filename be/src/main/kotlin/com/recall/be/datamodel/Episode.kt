package com.recall.be.datamodel

import com.recall.be.gremlin.TraversableToMany
import com.syncleus.ferma.FramedGraph
import com.syncleus.ferma.annotations.GraphElement
import com.syncleus.ferma.annotations.Property


@GraphElement
interface Episode: Node {

    @Property("name")
    fun getName(): String

    @Property("name")
    fun setName(name: String)
}

val FramedGraph.episodes: TraversableToMany<*, Episode> get() = traverse { it.V() }
