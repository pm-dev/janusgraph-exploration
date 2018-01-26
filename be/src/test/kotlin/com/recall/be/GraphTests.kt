package com.recall.be

import org.janusgraph.core.JanusGraph
import org.janusgraph.core.JanusGraphFactory
import org.janusgraph.example.GraphOfTheGodsFactory
import org.junit.Before
import org.junit.Test
import org.springframework.core.io.ClassPathResource

class GraphTests {

    lateinit var graph: JanusGraph

    @Before
    fun setup() {
		val configuration = ClassPathResource("janusgraph-configuration.properties").file.absolutePath
		graph = JanusGraphFactory.open(configuration)
		GraphOfTheGodsFactory.loadWithoutMixedIndex(graph, true)
    }

    @Test
    fun contextLoads() {
//        val one = graph.traversal().V().has("name", "saturn").next().asTitan()
//        Assertions.assertThat(one.age).isEqualTo(10000)
    }



}
