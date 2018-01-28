package com.recall.be.datamodel

import com.recall.be.gremlin.TraversableToMany
import com.recall.be.gremlin.TraversableToSingle
import com.syncleus.ferma.FramedGraph
import com.syncleus.ferma.VertexFrame
import com.syncleus.ferma.annotations.GraphElement

@GraphElement
interface Node: VertexFrame

val <T: Node> T.then: TraversableToSingle<*, T> get() = traverse { it.identity() }

val FramedGraph.nodes: TraversableToMany<*, Node> get() = traverse { it.V() }
