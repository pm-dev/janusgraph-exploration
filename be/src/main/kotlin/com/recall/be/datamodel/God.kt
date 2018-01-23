package com.recall.be.datamodel

import com.syncleus.ferma.AbstractVertexFrame
import com.syncleus.ferma.annotations.Property
import org.apache.tinkerpop.gremlin.structure.Vertex

//abstract class God: AbstractVertexFrame() {
//
//    val name get() = getProperty<String>("name")
//
//    @Property("")
//    val age: String
//}

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
