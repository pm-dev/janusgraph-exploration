package com.starwars.episode

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.framework.graphql.TraversalLoader
import org.springframework.stereotype.Component

@Component
class EpisodeQueryResolver(
        val loader: TraversalLoader
) : GraphQLQueryResolver

