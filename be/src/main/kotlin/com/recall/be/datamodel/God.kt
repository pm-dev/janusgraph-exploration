package com.recall.be.datamodel

import org.apache.tinkerpop.gremlin.structure.Vertex

class God(vertex: Vertex) {

    companion object {
        val label = "god"
    }

    val name = vertex.property<String>("name").value()

    val age = vertex.property<Int>("age").value()
}

fun Vertex.asGod(): God {
    return God(vertex = this)
}
