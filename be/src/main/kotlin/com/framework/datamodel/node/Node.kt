package com.framework.datamodel.node

import com.syncleus.ferma.AbstractVertexFrame

abstract class Node: AbstractVertexFrame() {

    fun id(): Long = getId()

    override fun hashCode(): Int = getId<Long>().hashCode()

    override fun equals(other: Any?): Boolean = other != null && other is Node && getId<Long>() == other.getId<Long>()
}
