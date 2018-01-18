package com.recall.be

import graphql.execution.instrumentation.Instrumentation
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentation
import org.dataloader.DataLoaderRegistry
import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import org.springframework.context.annotation.Bean


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
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
