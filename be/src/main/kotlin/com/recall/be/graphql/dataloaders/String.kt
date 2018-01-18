package com.recall.be.graphql.dataloaders

import org.dataloader.DataLoader
import org.dataloader.DataLoaderOptions
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class StringDataLoader(options: DataLoaderOptions): DataLoader<Int, String>({ keys ->
    CompletableFuture.supplyAsync({
        println("Supplying String Data Loader: $keys")
        listOf("Hello World")
    })
}, options)

