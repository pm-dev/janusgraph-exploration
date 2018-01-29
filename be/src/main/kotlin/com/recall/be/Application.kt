package com.recall.be

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
    fun dataLoaderRegistry(loaderList: List<DataLoader<*, *>>): DataLoaderRegistry =
            loaderList.fold(
                initial = DataLoaderRegistry(),
                operation = { registry, loader -> registry.register(loader.javaClass.simpleName, loader)})

    @Bean
    fun instrumentation(dataLoaderRegistry: DataLoaderRegistry): Instrumentation =
            DataLoaderDispatcherInstrumentation(dataLoaderRegistry)

    @Bean
    fun dataLoaderOptions(): DataLoaderOptions =
            DataLoaderOptions()

    @Bean
    fun graph(): JanusGraph =
            JanusGraphFactory.open(ClassPathResource("janusgraph-configuration.properties").file.absolutePath)
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
