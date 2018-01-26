package com.recall.be.datamodel

import com.syncleus.ferma.AbstractVertexFrame
import com.syncleus.ferma.annotations.GraphElement
import com.syncleus.ferma.annotations.Property

@GraphElement
abstract class Human: Character, AbstractVertexFrame() {

    @Property("homePlanet")
    abstract fun getHomePlanet(): String?

    @Property("homePlanet")
    abstract fun setHomePlanet(homePlanet: String)
}
