package com.recall.be.datamodel

import com.recall.be.gremlin.TraversableToMany
import com.syncleus.ferma.AbstractVertexFrame
import com.syncleus.ferma.FramedGraph
import com.syncleus.ferma.annotations.GraphElement
import com.syncleus.ferma.annotations.Property

@GraphElement
abstract class Droid: AbstractVertexFrame(), Character {

    @Property("primaryFunction")
    abstract fun getPrimaryFunction(): String

    @Property("primaryFunction")
    abstract fun setPrimaryFunction(primaryFunction: String)
}

val FramedGraph.droids: TraversableToMany<*, Droid> get() = traverse { it.V() }
