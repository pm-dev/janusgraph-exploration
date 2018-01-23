package com.recall.be.datamodel

import com.google.common.base.Preconditions
import org.apache.tinkerpop.gremlin.process.traversal.Order
import org.apache.tinkerpop.gremlin.structure.Direction
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.T
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.janusgraph.core.JanusGraph
import org.janusgraph.core.Multiplicity
import org.janusgraph.core.attribute.Geoshape
import org.janusgraph.core.schema.ConsistencyModifier
import org.janusgraph.graphdb.database.StandardJanusGraph

private val ERR_NO_INDEXING_BACKEND = "The indexing backend with name \"%s\" is not defined. Specify an existing indexing backend or " +
    "use GraphOfTheGodsFactory.loadWithoutMixedIndex(graph,true) to load without the use of an " +
    "indexing backend."

private fun mixedIndexNullOrExists(graph: StandardJanusGraph, indexName: String?): Boolean {
    return indexName == null || graph.indexSerializer.containsIndex(indexName)
}

fun load(graph: JanusGraph, mixedIndexName: String?, uniqueNameCompositeIndex: Boolean) {
    if (graph is StandardJanusGraph) {
        Preconditions.checkState(mixedIndexNullOrExists(graph, mixedIndexName),
                ERR_NO_INDEXING_BACKEND, mixedIndexName)
    }

    //Create Schema
    val management = graph.openManagement()
    val name = management.makePropertyKey("name").dataType(String::class.java).make()
    val nameIndexBuilder = management.buildIndex("name", Vertex::class.java).addKey(name)
    if (uniqueNameCompositeIndex)
        nameIndexBuilder.unique()
    val nameIndex = nameIndexBuilder.buildCompositeIndex()
    management.setConsistency(nameIndex, ConsistencyModifier.LOCK)
    val age = management.makePropertyKey("age").dataType(Int::class.java).make()
    if (null != mixedIndexName)
        management.buildIndex("vertices", Vertex::class.java).addKey(age).buildMixedIndex(mixedIndexName)

    val time = management.makePropertyKey("time").dataType(Int::class.java).make()
    val reason = management.makePropertyKey("reason").dataType(String::class.java).make()
    val place = management.makePropertyKey("place").dataType(Geoshape::class.java).make()
    if (null != mixedIndexName)
        management.buildIndex("edges", Edge::class.java).addKey(reason).addKey(place).buildMixedIndex(mixedIndexName)

    management.makeEdgeLabel("father").multiplicity(Multiplicity.MANY2ONE).make()
    management.makeEdgeLabel("mother").multiplicity(Multiplicity.MANY2ONE).make()
    val battled = management.makeEdgeLabel("battled").signature(time).make()
    management.buildEdgeIndex(battled, "battlesByTime", Direction.BOTH, Order.decr, time)
    management.makeEdgeLabel("lives").signature(reason).make()
    management.makeEdgeLabel("pet").make()
    management.makeEdgeLabel("brother").make()

    management.makeVertexLabel("titan").make()
    management.makeVertexLabel("location").make()
    management.makeVertexLabel("god").make()
    management.makeVertexLabel("demigod").make()
    management.makeVertexLabel("human").make()
    management.makeVertexLabel("monster").make()

    management.commit()

    val tx = graph.newTransaction()
    // vertices

    val saturn = tx.addVertex(T.label, "titan", "name", "saturn", "age", 10000)
    val sky = tx.addVertex(T.label, "location", "name", "sky")
    val sea = tx.addVertex(T.label, "location", "name", "sea")
    val jupiter = tx.addVertex(T.label, "god", "name", "jupiter", "age", 5000)
    val neptune = tx.addVertex(T.label, "god", "name", "neptune", "age", 4500)
    val hercules = tx.addVertex(T.label, "demigod", "name", "hercules", "age", 30)
    val alcmene = tx.addVertex(T.label, "human", "name", "alcmene", "age", 45)
    val pluto = tx.addVertex(T.label, "god", "name", "pluto", "age", 4000)
    val nemean = tx.addVertex(T.label, "monster", "name", "nemean")
    val hydra = tx.addVertex(T.label, "monster", "name", "hydra")
    val cerberus = tx.addVertex(T.label, "monster", "name", "cerberus")
    val tartarus = tx.addVertex(T.label, "location", "name", "tartarus")

    // edges

    jupiter.addEdge("father", saturn)
    jupiter.addEdge("lives", sky, "reason", "loves fresh breezes")
    jupiter.addEdge("brother", neptune)
    jupiter.addEdge("brother", pluto)

    neptune.addEdge("lives", sea).property("reason", "loves waves")
    neptune.addEdge("brother", jupiter)
    neptune.addEdge("brother", pluto)

    hercules.addEdge("father", jupiter)
    hercules.addEdge("mother", alcmene)
    hercules.addEdge("battled", nemean, "time", 1, "place", Geoshape.point(38.1, 23.7))
    hercules.addEdge("battled", hydra, "time", 2, "place", Geoshape.point(37.7, 23.9))
    hercules.addEdge("battled", cerberus, "time", 12, "place", Geoshape.point(39.0, 22.0))

    pluto.addEdge("brother", jupiter)
    pluto.addEdge("brother", neptune)
    pluto.addEdge("lives", tartarus, "reason", "no fear of death")
    pluto.addEdge("pet", cerberus)

    cerberus.addEdge("lives", tartarus)

    // commit the transaction to disk
    tx.commit()
}
