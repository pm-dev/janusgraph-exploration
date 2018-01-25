package com.recall.be

import com.recall.be.datamodel.Character
import com.recall.be.datamodel.Droid
import com.recall.be.datamodel.Episode
import com.recall.be.datamodel.Human
import com.syncleus.ferma.DelegatingFramedGraph
import com.syncleus.ferma.FramedGraph
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentation
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import org.dataloader.DataLoaderRegistry
import org.janusgraph.core.JanusGraph
import org.janusgraph.core.JanusGraphFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource


@SpringBootApplication
class Application {

    @Bean
    fun dataLoaderRegistry(loaderList: List<DataLoader<*, *>>): DataLoaderRegistry {
        val registry = DataLoaderRegistry()
        for (loader in loaderList) {
            registry.register(loader.javaClass.simpleName, loader)
        }
        return registry
    }

    @Bean
    fun instrumentation(dataLoaderRegistry: DataLoaderRegistry): Instrumentation {
        return DataLoaderDispatcherInstrumentation(dataLoaderRegistry)
    }

    @Bean
    fun dataLoaderOptions() = DataLoaderOptions()

    @Bean
    fun graph(): JanusGraph {
        val configuration = ClassPathResource("janusgraph-configuration.properties").file.absolutePath
        return JanusGraphFactory.open(configuration)
    }

    @Bean
    fun framedGraph(graph: JanusGraph): FramedGraph {
        val types = setOf(
                Character::class.java,
                Droid::class.java,
                Human::class.java,
                Episode::class.java)
        return DelegatingFramedGraph<JanusGraph>(graph, true, types)
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
