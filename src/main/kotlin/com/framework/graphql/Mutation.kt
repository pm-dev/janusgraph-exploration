package com.framework.graphql

import com.syncleus.ferma.FramedGraph

abstract class Mutation<out T>{

    open fun checkPermissions(graph: FramedGraph): Boolean = true

    abstract fun run(graph: FramedGraph): T
}
