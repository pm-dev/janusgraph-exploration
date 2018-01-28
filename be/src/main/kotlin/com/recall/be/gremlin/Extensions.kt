package com.recall.be.gremlin

import com.recall.be.GraphTraversalToMany
import com.recall.be.GraphTraversalToOptional
import com.recall.be.GraphTraversalToSingle
import com.syncleus.ferma.*
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.process.traversal.step.map.GraphStep
import org.apache.tinkerpop.gremlin.structure.Vertex


fun GraphTraversalSource.verticies(vararg vertexIds: Any): GraphTraversalToMany<Vertex, Vertex> {
    val clone = this.clone()
    clone.bytecode.addStep(GraphTraversal.Symbols.V, *vertexIds)
    val traversal = GraphTraversalToMany<Vertex, Vertex>(clone)
    return traversal.addStep(GraphStep<Any, Vertex>(traversal, Vertex::class.java, true, *vertexIds)) as GraphTraversalToMany<Vertex, Vertex>
}

fun GraphTraversalSource.vertex(vertexId: Any): GraphTraversalToOptional<Vertex, Vertex> =
        verticies(vertexId).toOptional()

fun <FROM, TO> DelegatingFramedGraph<*>.traverseToMany(
        traverser: (GraphTraversalSource) -> GraphTraversalToMany<*, *>
) = TraversableToMany(traverser(baseGraph.traversal()), this) as TraversableToMany<FROM, TO>

fun <FROM, TO> DelegatingFramedGraph<*>.traverseToOptional(
        traverser: (GraphTraversalSource) -> GraphTraversalToOptional<*, *>
) = TraversableToOptional(traverser(baseGraph.traversal()), this) as TraversableToOptional<FROM, TO>

fun <FROM, TO> DelegatingFramedGraph<*>.traverseToSingle(
        traverser: (GraphTraversalSource) -> GraphTraversalToSingle<*, *>
) = TraversableToSingle(traverser(baseGraph.traversal()), this) as TraversableToSingle<FROM, TO>



class TraversableToOptional<FROM, TO>(
        val baseTraversal: GraphTraversalToOptional<FROM, TO>,
        val parentGraph: FramedGraph
): DefaultTraversable<FROM, TO>(baseTraversal, parentGraph) {

    fun has(property: String, value: Any) = traverseToOptional { it.has(property, value) }

    fun toSingle(): TraversableToSingle<FROM, TO> {
        return TraversableToSingle(baseTraversal.toSingle(), parentGraph)
    }

    fun <NEXT> traverseToSingle(
            traversal:(GraphTraversalToOptional<FROM, TO>) -> GraphTraversalToSingle<FROM, NEXT>
    ) =
            TraversableToSingle(traversal(baseTraversal), parentGraph)

    fun <NEXT> traverseToOptional(
            traversal:(GraphTraversalToOptional<FROM, TO>) -> GraphTraversalToOptional<FROM, NEXT>
    ) =
            TraversableToOptional(traversal(baseTraversal), parentGraph)

    fun <NEXT> traverseToMany(
            traversal:(GraphTraversalToOptional<FROM, TO>) -> GraphTraversalToMany<FROM, NEXT>
    ) =
            TraversableToMany(traversal(baseTraversal), parentGraph)
}

class TraversableToSingle<FROM, TO>(
        val baseTraversal: GraphTraversalToSingle<FROM, TO>,
        val parentGraph: FramedGraph
): DefaultTraversable<FROM, TO>(
        baseTraversal,
        parentGraph
) {

    fun has(property: String, value: Any) = traverseToOptional { it.has(property, value) }

    fun toOptional(): TraversableToOptional<FROM, TO> {
        return TraversableToOptional(baseTraversal.toOptional(), parentGraph)
    }

    fun <NEXT> traverseToSingle(
            traversal:(GraphTraversalToSingle<FROM, TO>) -> GraphTraversalToSingle<FROM, NEXT>
    ) =
            TraversableToSingle(traversal(baseTraversal), parentGraph)

    fun <NEXT> traverseToOptional(
            traversal:(GraphTraversalToSingle<FROM, TO>) -> GraphTraversalToOptional<FROM, NEXT>
    ) =
            TraversableToOptional(traversal(baseTraversal), parentGraph)

    fun <NEXT> traverseToMany(
            traversal:(GraphTraversalToSingle<FROM, TO>) -> GraphTraversalToMany<FROM, NEXT>
    ) =
            TraversableToMany(traversal(baseTraversal), parentGraph)

}

class TraversableToMany<FROM, TO>(
        val baseTraversal: GraphTraversalToMany<FROM, TO>,
        val parentGraph: FramedGraph
): DefaultTraversable<FROM, TO>(baseTraversal, parentGraph) {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                

    fun has(property: String, value: Any) = traverseToMany { it.has(property, value) }

    fun hasId(value: Any) = traverseToOptional { it.hasId(value) }

    fun toOptional(): TraversableToOptional<FROM, TO> {
        return TraversableToOptional(baseTraversal.toOptional(), parentGraph)
    }

    fun toSingle(): TraversableToSingle<FROM, TO> {
        return TraversableToSingle(baseTraversal.toSingle(), parentGraph)
    }

    fun <NEXT> traverseToSingle(
            traversal:(GraphTraversalToMany<FROM, TO>) -> GraphTraversalToSingle<FROM, NEXT>
    ) =
            TraversableToSingle(traversal(baseTraversal), parentGraph)

    fun <NEXT> traverseToOptional(
            traversal:(GraphTraversalToMany<FROM, TO>) -> GraphTraversalToOptional<FROM, NEXT>
    ) =
            TraversableToOptional(traversal(baseTraversal), parentGraph)

    fun <NEXT> traverseToMany(
            traversal:(GraphTraversalToMany<FROM, TO>) -> GraphTraversalToMany<FROM, NEXT>
    ) =
            TraversableToMany(traversal(baseTraversal), parentGraph)
}
