package com.recall.be.datamodel

import com.syncleus.ferma.AbstractVertexFrame
import com.syncleus.ferma.annotations.Property

abstract class Human: AbstractVertexFrame(), Character {

    @Property("homePlanet")
    abstract fun getHomePlanet(): String?

    @Property("homePlanet")
    abstract fun setHomePlanet(homePlanet: String?)
}
