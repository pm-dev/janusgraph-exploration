package com.recall.be.datamodel

import com.syncleus.ferma.annotations.GraphElement
import com.syncleus.ferma.annotations.Property


@GraphElement
interface Episode {

    @Property("name")
    fun getName(): String

    @Property("name")
    fun setName(name: String)
}
