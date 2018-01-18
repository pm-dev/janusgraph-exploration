package com.recall.be

import org.janusgraph.core.JanusGraph
import org.janusgraph.example.GraphOfTheGodsFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class ApplicationReadyListener(val graph: JanusGraph): ApplicationListener<ApplicationReadyEvent> {

    override fun onApplicationEvent(event: ApplicationReadyEvent?) {
        GraphOfTheGodsFactory.loadWithoutMixedIndex(graph, true)
        println("Did load Graph of the Gods")
    }
}
