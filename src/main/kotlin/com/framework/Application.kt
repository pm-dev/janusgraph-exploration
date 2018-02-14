package com.framework

import org.janusgraph.core.JanusGraph
import org.janusgraph.core.JanusGraphFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.core.io.ClassPathResource


@SpringBootApplication
@ComponentScan(basePackages = ["com.framework", "com.starwars"])
class Application {

//    @Bean
//    fun dataLoaderRegistry(loaderList: List<DataLoader<*, *>>): DataLoaderRegistry =
//            loaderList.fold(
//                initial = DataLoaderRegistry(),
//                operation = { registry, loader -> registry.register(loader.javaClass.simpleName, loader)})
//
//    @Bean
//    fun instrumentation(dataLoaderRegistry: DataLoaderRegistry): Instrumentation =
//            DataLoaderDispatcherInstrumentation(dataLoaderRegistry)

    @Bean
    fun graph(): JanusGraph =
            JanusGraphFactory.open(ClassPathResource("janusgraph-configuration.properties").file.absolutePath)
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
