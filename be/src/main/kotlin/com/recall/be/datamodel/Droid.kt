package com.recall.be.datamodel

import com.syncleus.ferma.AbstractVertexFrame
import com.syncleus.ferma.annotations.GraphElement
import com.syncleus.ferma.annotations.Property

@GraphElement
abstract class Droid: AbstractVertexFrame(), Character {

    @Property("primaryFunction")
    abstract fun getPrimaryFunction(): String

    @Property("primaryFunction")
    abstract fun setPrimaryFunction(primaryFunction: String)
}
