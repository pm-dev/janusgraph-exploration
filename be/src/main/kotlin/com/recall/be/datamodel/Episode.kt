package com.recall.be.datamodel

import com.syncleus.ferma.AbstractVertexFrame
import com.syncleus.ferma.VertexFrame
import com.syncleus.ferma.annotations.Property

interface Episode: VertexFrame {

    @Property("name")
    fun getName(): String

    @Property("name")
    fun setName(name: String)
}
