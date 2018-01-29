package com.recall.be

import com.recall.be.datamodel.*
import com.recall.be.gremlin.TraversalLoader
import com.recall.be.gremlin.fetchSingle
import com.syncleus.ferma.DelegatingFramedGraph
import com.syncleus.ferma.Traversable
import org.assertj.core.api.Assertions
import org.dataloader.DataLoaderOptions
import org.janusgraph.core.JanusGraph
import org.janusgraph.core.JanusGraphFactory
import org.janusgraph.example.GraphOfTheGodsFactory
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource

class GraphTests {

    lateinit var graph: StarwarsGraph
    lateinit var loader: TraversalLoader

    @Before
    fun setup() {
		val configuration = ClassPathResource("janusgraph-configuration.properties").file.absolutePath
		val janus = JanusGraphFactory.open(configuration)
        graph = StarwarsGraph(janus)
        loader = TraversalLoader(graph, DataLoaderOptions())

    }

    @Test
    fun contextLoads() {
        val luke = graph.traverse<Traversable<*, Character>> { it.V().has("name", "Luke Skywalker") }.next(Human::class.java)
        Assertions.assertThat(luke.getName()).isEqualTo("Luke Skywalker")
        Assertions.assertThat(luke.getHomePlanet()).isEqualTo("Tatooine")
    }
}
