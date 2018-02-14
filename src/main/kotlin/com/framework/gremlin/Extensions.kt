package com.framework.gremlin

import com.framework.datamodel.edges.Traversal
import com.framework.graphql.Mutation
import com.framework.graphql.optional
import com.syncleus.ferma.FramedGraph
import com.syncleus.ferma.Traversable
import com.syncleus.ferma.VertexFrame
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Vertex


fun <FROM: VertexFrame> Traversal.Bound<FROM, *>.asGremlin(
        source: GraphTraversalSource
): GraphTraversal<Vertex, Vertex> = relationship.hops.fold(
        initial = source.V(froms.map { from -> from.getId<Long>() }),
        operation = { traversal, hop -> traversal.out(hop.name) })

fun GraphTraversalSource.vertexIds(vertexIds: Collection<Long>) = V(*vertexIds.toTypedArray())

inline fun <reified T: VertexFrame> FramedGraph.insert(): T = addFramedVertex(T::class.java)

inline fun <reified T: VertexFrame> FramedGraph.fetchOptional(
        noinline traversal: (GraphTraversalSource) -> GraphTraversal<*, *>
): T? = traverse<Traversable<*, *>> { traversal(it) }.toList(T::class.java).optional()

inline fun <reified T: VertexFrame> FramedGraph.fetchSingle(
        noinline traversal: (GraphTraversalSource) -> GraphTraversal<*, *>
): T = traverse<Traversable<*, *>> { traversal(it) }.toList(T::class.java).single()

inline fun <reified T: VertexFrame> FramedGraph.fetchMany(
        noinline traversal: (GraphTraversalSource) -> GraphTraversal<*, *>
): List<T> = traverse<Traversable<*, *>> { traversal(it) }.toList(T::class.java)


fun <T> FramedGraph.mutate(mutation: Mutation<T>): T {
    if (!mutation.checkPermissions(this)) {
        throw Exception("Mutation permissions failed")
    }
    val result = mutation.run(this)
    tx().commit()
    return result
}
