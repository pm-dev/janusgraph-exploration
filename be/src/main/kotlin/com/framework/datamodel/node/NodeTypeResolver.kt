package com.framework.datamodel.node

interface NodeTypeResolver {

    fun getId(node: Node): Long = node.getId<Long>()
}
