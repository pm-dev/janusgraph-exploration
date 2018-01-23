package com.recall.be.graphql.resolvers

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.recall.be.datamodel.Titan
import com.recall.be.datamodel.asTitan
import com.recall.be.graphql.dataloaders.VertexDataLoader
import com.recall.be.graphql.dataloaders.loadTitan
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class Query(
        val dataLoader: VertexDataLoader
): GraphQLQueryResolver {

    fun helloWorld(environment: DataFetchingEnvironment): CompletableFuture<Titan> = dataLoader.loadTitan { source ->
        Thread.sleep(5 * 1000)
        source.V().has("name", "saturn")
    }

    fun helloWorld2(environment: DataFetchingEnvironment): CompletableFuture<List<Titan>> = dataLoader.loadMany(
            listOf(
                    { source -> source.V().has("name", "saturn") },
                    { source -> source.V().has("name", "saturn") },
                    { source -> source.V().has("name", "saturn") },
                    { source -> source.V().has("name", "saturn") },
                    { source -> source.V().has("name", "saturn") },
                    { source -> source.V().has("name", "saturn") },
                    { source -> source.V().has("name", "saturn") },
                    { source -> source.V().has("name", "saturn") },
                    { source -> source.V().has("name", "saturn") },
                    { source -> source.V().has("name", "saturn") },
                    { source -> source.V().has("name", "saturn") },
                    { source -> source.V().has("name", "saturn") },
                    { source -> source.V().has("name", "saturn") }))
            .thenApply { it.map {  it.single().asTitan() } }
}
