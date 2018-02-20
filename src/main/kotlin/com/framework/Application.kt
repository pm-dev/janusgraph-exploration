package com.framework

import org.janusgraph.core.JanusGraph
import org.janusgraph.core.JanusGraphFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.core.io.ClassPathResource


@SpringBootApplication
@ComponentScan(basePackages = ["com"])
class Application {

    @Bean
    fun graph(): JanusGraph =
            JanusGraphFactory.open(ClassPathResource("janusgraph-configuration.properties").file.absolutePath)
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
