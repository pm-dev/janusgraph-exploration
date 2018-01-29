package com.recall.be.gremlin

import com.recall.be.relationships.Traversal
import com.syncleus.ferma.VertexFrame
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Vertex


fun <FROM: VertexFrame> Traversal.Bound<FROM, *>.toGremlin(
        source: GraphTraversalSource
): GraphTraversal<Vertex, Vertex> {
    return relationship.hops.fold(
            initial = source.V(froms.map { from -> from.getId<Long>() }),
            operation = { traversal, hop -> traversal.out(hop.name) })
}
