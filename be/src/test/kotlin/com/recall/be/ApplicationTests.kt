package com.recall.be

import org.janusgraph.core.JanusGraph
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class ApplicationTests {

	@Autowired lateinit var graph: JanusGraph

	@Before
	fun setup() {
//		val configuration = ClassPathResource("janusgraph-configuration.properties").file.absolutePath
//		graph = JanusGraphFactory.open(configuration)
//		GraphOfTheGodsFactory.loadWithoutMixedIndex(graph, true)
	}

	@Test
	fun contextLoads() {
//		val one = graph.traversal().V().has("name", "saturn").next().asTitan()
//		Assertions.assertThat(one.age).isEqualTo(10000)
	}
}
