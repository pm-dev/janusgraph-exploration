package com.framework.datamodel.edges


interface Traversal<FROM, TO> {

    val relationship: Relationship<FROM, TO>

    interface Bound<FROM, TO>: Traversal<FROM, TO> {
        val froms: Collection<FROM>
    }

    interface SingleBound<FROM, TO>: Bound<FROM, TO> {
        val from: FROM
        override val froms: Collection<FROM> get() = listOf(from)
    }

    interface MultiBound<FROM, TO>: Bound<FROM, TO>

    interface ToOptional<FROM, TO>: Traversal<FROM, TO> {
        override val relationship: Relationship.ToOptional<FROM, TO>
        fun toMultiBound(): MultiBoundToOptional<FROM, TO>
    }

    interface ToSingle<FROM, TO>: Traversal<FROM, TO> {
        override val relationship: Relationship.ToSingle<FROM, TO>
        fun toMultiBound(): MultiBoundToSingle<FROM, TO>
    }

    interface ToMany<FROM, TO>: Traversal<FROM, TO> {
        override val relationship: Relationship.ToMany<FROM, TO>
        fun toMultiBound(): MultiBoundToMany<FROM, TO>
    }

    interface BoundToMany<FROM, TO>: Bound<FROM, TO>, ToMany<FROM, TO>

    interface BoundToOptional<FROM, TO>: Bound<FROM, TO>, ToOptional<FROM, TO>

    interface BoundToSingle<FROM, TO>: Bound<FROM, TO>, ToSingle<FROM, TO>

    data class SingleBoundToMany<FROM, TO>(
            override val from: FROM,
            override val relationship: Relationship.ToMany<FROM, TO>
    ): SingleBound<FROM, TO>, BoundToMany<FROM, TO> {
        override fun toMultiBound() = MultiBoundToMany(froms = listOf(from), relationship = relationship)
    }

    data class MultiBoundToMany<FROM, TO>(
            override val froms: Collection<FROM>,
            override val relationship: Relationship.ToMany<FROM, TO>
    ): MultiBound<FROM, TO>, BoundToMany<FROM, TO> {
        override fun toMultiBound() = this
    }

    data class SingleBoundToOptional<FROM, TO>(
            override val from: FROM,
            override val relationship: Relationship.ToOptional<FROM, TO>
    ): SingleBound<FROM, TO>, BoundToOptional<FROM, TO> {
        override fun toMultiBound() = MultiBoundToOptional(froms = listOf(from), relationship = relationship)
    }

    data class MultiBoundToOptional<FROM, TO>(
            override val froms: Collection<FROM>,
            override val relationship: Relationship.ToOptional<FROM, TO>
    ): MultiBound<FROM, TO>, BoundToOptional<FROM, TO> {
        override fun toMultiBound() = this
    }

    data class SingleBoundToSingle<FROM, TO>(
            override val from: FROM,
            override val relationship: Relationship.ToSingle<FROM, TO>
    ): SingleBound<FROM, TO>, BoundToSingle<FROM, TO> {
        override fun toMultiBound() = MultiBoundToSingle(froms = listOf(from), relationship = relationship)
    }

    data class MultiBoundToSingle<FROM, TO>(
            override val froms: Collection<FROM>,
            override val relationship: Relationship.ToSingle<FROM, TO>
    ): MultiBound<FROM, TO>, BoundToSingle<FROM, TO> {
        override fun toMultiBound() = this
    }
}

fun <FROM, TO> FROM.traverse(relationship: Relationship.ToOptional<FROM, TO>) =
        Traversal.SingleBoundToOptional(from = this, relationship = relationship)

fun <FROM, TO> Collection<FROM>.traverse(relationship: Relationship.ToOptional<FROM, TO>) =
        Traversal.MultiBoundToOptional(froms = this, relationship = relationship)

fun <FROM, TO> FROM.traverse(relationship: Relationship.ToSingle<FROM, TO>) =
        Traversal.SingleBoundToSingle(from = this, relationship = relationship)

fun <FROM, TO> Collection<FROM>.traverse(relationship: Relationship.ToSingle<FROM, TO>) =
        Traversal.MultiBoundToSingle(froms = this, relationship = relationship)

fun <FROM, TO> FROM.traverse(relationship: Relationship.ToMany<FROM, TO>) =
        Traversal.SingleBoundToMany(from = this, relationship = relationship)

fun <FROM, TO> Collection<FROM>.traverse(relationship: Relationship.ToMany<FROM, TO>) =
        Traversal.MultiBoundToMany(froms = this, relationship = relationship)
