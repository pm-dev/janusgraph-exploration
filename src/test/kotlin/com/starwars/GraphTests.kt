package com.starwars

import com.starwars.character.Character
import com.starwars.human.Human
import com.syncleus.ferma.Traversable
import org.assertj.core.api.Assertions
import org.janusgraph.core.JanusGraphFactory
import org.junit.Before
import org.junit.Test
import org.springframework.core.io.ClassPathResource

class GraphTests {

    lateinit var graph: StarwarsGraph

    @Before
    fun setup() {
		val configuration = ClassPathResource("janusgraph-configuration.properties").file.absolutePath
		val janus = JanusGraphFactory.open(configuration)
        graph = StarwarsGraph(janus)
    }

    @Test
    fun contextLoads() {
        val luke = graph.traverse<Traversable<*, Character>> { it.V().has("name", "Luke Skywalker") }.next(Human::class.java)
        Assertions.assertThat(luke.getName()).isEqualTo("Luke Skywalker")
        Assertions.assertThat(luke.getHomePlanet()).isEqualTo("Tatooine")
    }
}
