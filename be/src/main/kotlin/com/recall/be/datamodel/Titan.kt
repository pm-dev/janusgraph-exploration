package com.recall.be.datamodel

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Vertex

class Titan(vertex: Vertex) {

    companion object {
        val label = "titan"
    }

    val name = vertex.property<String>("name").value()

    val age = vertex.property<Int>("age").value()
}


fun Vertex.asTitan(): Titan {
    return Titan(vertex = this)
}
