package com.recall.be

import graphql.execution.instrumentation.Instrumentation
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentation
import org.apache.commons.configuration.Configuration
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.dataloader.DataLoaderRegistry
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import org.janusgraph.core.JanusGraph
import org.janusgraph.core.JanusGraphFactory
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


}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
