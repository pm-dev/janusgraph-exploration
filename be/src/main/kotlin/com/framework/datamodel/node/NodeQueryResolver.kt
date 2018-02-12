package com.framework.datamodel.node

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.framework.graphql.TraversalLoader
import com.framework.graphql.fetchOptional
import com.syncleus.ferma.VertexFrame
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component


@Component
class NodeQueryResolver(
        val loader: TraversalLoader
): GraphQLQueryResolver {

    fun node(id: Long, environment: DataFetchingEnvironment) =
            loader.fetchOptional<VertexFrame> { it.V(id) }
}
