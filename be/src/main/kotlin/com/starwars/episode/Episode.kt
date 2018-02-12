package com.starwars.episode

import com.framework.datamodel.node.Node
import com.syncleus.ferma.annotations.Property

abstract class Episode: Node() {

    @Property("name")
    abstract fun getName(): String

    @Property("name")
    abstract fun setName(name: String)
}
