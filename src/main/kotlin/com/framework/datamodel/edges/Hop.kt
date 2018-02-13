package com.framework.datamodel.edges


interface Hop<FROM, TO>: Relationship<FROM, TO> {

    override val inverse: Hop<TO, FROM>

    override val hops: List<Hop<*, *>> get() = listOf(this)

    val name: String

    val direction: Direction?

    val isSymmetric: Boolean get() = direction == null

    val type: Type

    enum class Direction {
        FORWARD,
        BACKWARD;

        val inverse: Direction
            get() = when (this) {
                FORWARD -> BACKWARD
                BACKWARD -> FORWARD
            }
    }

    enum class Type {
        ASYMMETRIC_SINGLE_TO_OPTIONAL,
        ASYMMETRIC_OPTIONAL_TO_SINGLE,
        ASYMMETRIC_OPTIONAL_TO_OPTIONAL,
        ASYMMETRIC_SINGLE_TO_SINGLE,
        ASYMMETRIC_MANY_TO_SINGLE,
        ASYMMETRIC_MANY_TO_OPTIONAL,
        ASYMMETRIC_SINGLE_TO_MANY,
        ASYMMETRIC_OPTIONAL_TO_MANY,
        ASYMMETRIC_MANY_TO_MANY,
        SYMMETRIC_OPTIONAL_TO_OPTIONAL,
        SYMMETRIC_SINGLE_TO_SINGLE,
        SYMMETRIC_MANY_TO_MANY,
    }

    interface FromOne<FROM, TO>: Hop<FROM, TO>, Relationship.FromOne<FROM, TO> {
        override val inverse: ToOne<TO, FROM>
    }

    interface ToOne<FROM, TO>: Hop<FROM, TO>, Relationship.ToOne<FROM, TO> {
        override val inverse: FromOne<TO, FROM>
    }

    interface FromOptional<FROM, TO>: FromOne<FROM, TO>, Relationship.FromOptional<FROM, TO> {
        override val inverse: ToOptional<TO, FROM>
    }

    interface ToOptional<FROM, TO>: ToOne<FROM, TO>, Relationship.ToOptional<FROM, TO> {
        override val inverse: FromOptional<TO, FROM>
    }

    interface FromSingle<FROM, TO>: FromOne<FROM, TO>, Relationship.FromSingle<FROM, TO> {
        override val inverse: ToSingle<TO, FROM>
    }

    interface ToSingle<FROM, TO>: ToOne<FROM, TO>, Relationship.ToSingle<FROM, TO> {
        override val inverse: FromSingle<TO, FROM>
    }

    interface FromMany<FROM, TO>: Hop<FROM, TO>, Relationship.FromMany<FROM, TO> {
        override val inverse: ToMany<TO, FROM>
    }

    interface ToMany<FROM, TO>: Hop<FROM, TO>, Relationship.ToMany<FROM, TO> {
        override val inverse: FromMany<TO, FROM>
    }

    interface SymmetricOneToOne<TYPE>: FromOne<TYPE, TYPE>, ToOne<TYPE, TYPE>, Relationship.OneToOne<TYPE, TYPE> {
        override val inverse: SymmetricOneToOne<TYPE>
    }

    data class AsymmetricOptionalToOptional<FROM, TO>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD):
            FromOptional<FROM, TO>, ToOptional<FROM, TO>, Relationship.OptionalToOptional<FROM, TO> {

        override val inverse: AsymmetricOptionalToOptional<TO, FROM>
            get() =
                AsymmetricOptionalToOptional(name = name, direction = direction.inverse)

        override val type: Type get() = Type.ASYMMETRIC_OPTIONAL_TO_OPTIONAL
    }

    data class AsymmetricOptionalToSingle<FROM, TO>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD):
            FromOptional<FROM, TO>, ToSingle<FROM, TO>, Relationship.OptionalToSingle<FROM, TO> {

        override val inverse: AsymmetricSingleToOptional<TO, FROM>
            get() =
                AsymmetricSingleToOptional(name = name, direction = direction.inverse)

        override val type: Type get() = Type.ASYMMETRIC_OPTIONAL_TO_SINGLE
    }

    data class AsymmetricSingleToOptional<FROM, TO>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD):
            FromSingle<FROM, TO>, ToOptional<FROM, TO>, Relationship.SingleToOptional<FROM, TO> {

        override val inverse: AsymmetricOptionalToSingle<TO, FROM>
            get() =
                AsymmetricOptionalToSingle(name = name, direction = direction.inverse)

        override val type: Type get() = Type.ASYMMETRIC_SINGLE_TO_OPTIONAL
    }

    data class AsymmetricSingleToSingle<FROM, TO>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD):
            FromSingle<FROM, TO>, ToSingle<FROM, TO>, Relationship.SingleToSingle<FROM, TO> {

        override val inverse: AsymmetricSingleToSingle<TO, FROM>
            get() =
                AsymmetricSingleToSingle(name = name, direction = direction.inverse)

        override val type: Type get() = Type.ASYMMETRIC_SINGLE_TO_SINGLE
    }

    data class SymmetricOptionalToOptional<TYPE>(
            override val name: String) :
            SymmetricOneToOne<TYPE>, FromOptional<TYPE, TYPE>, ToOptional<TYPE, TYPE>, Relationship.OptionalToOptional<TYPE, TYPE> {

        override val direction: Direction? get() = null

        override val inverse: SymmetricOptionalToOptional<TYPE> get() = this

        override val type: Type get() = Type.SYMMETRIC_OPTIONAL_TO_OPTIONAL
    }

    data class SymmetricSingleToSingle<TYPE>(
            override val name: String) :
            SymmetricOneToOne<TYPE>, FromSingle<TYPE, TYPE>, ToSingle<TYPE, TYPE>, Relationship.SingleToSingle<TYPE, TYPE> {

        override val direction: Direction? get() = null

        override val inverse: SymmetricSingleToSingle<TYPE> get() = this

        override val type: Type get() = Type.SYMMETRIC_SINGLE_TO_SINGLE
    }

    data class AsymmetricSingleToMany<FROM, TO>(
            override val name: String) :
            FromSingle<FROM, TO>, ToMany<FROM, TO>, Relationship.SingleToMany<FROM, TO> {

        override val direction: Direction? get() = Direction.FORWARD

        override val inverse: AsymmetricManyToSingle<TO, FROM>
            get() =
                AsymmetricManyToSingle(name = name)

        override val type: Type get() = Type.ASYMMETRIC_SINGLE_TO_MANY
    }

    data class AsymmetricOptionalToMany<FROM, TO>(
            override val name: String) :
            FromOptional<FROM, TO>, ToMany<FROM, TO>, Relationship.OptionalToMany<FROM, TO> {

        override val direction: Direction? get() = Direction.FORWARD

        override val inverse: AsymmetricManyToOptional<TO, FROM>
            get() =
                AsymmetricManyToOptional(name = name)

        override val type: Type get() = Type.ASYMMETRIC_OPTIONAL_TO_MANY
    }

    /**
     * We restrict creating ManyToOne datamodel by clients to prevent creation of a
     * ManyToOne relationship that is equivalent in meaning to an already defined OneToMany
     * relationship, but using a different name. To get a ManyToOne relationship, define it
     * as its OneToMany equivalent then get its inverse.
     */
    data class AsymmetricManyToOptional<FROM, TO> internal constructor(
            override val name: String) :
            FromMany<FROM, TO>, ToOptional<FROM, TO>, Relationship.ManyToOptional<FROM, TO> {

        override val direction: Direction? get() = Direction.BACKWARD

        override val inverse: AsymmetricOptionalToMany<TO, FROM>
            get() =
                AsymmetricOptionalToMany(name = name)

        override val type: Type get() = Type.ASYMMETRIC_MANY_TO_OPTIONAL
    }

    /**
     * We restrict creating ManyToOne datamodel by clients to prevent creation of a
     * ManyToOne relationship that is equivalent in meaning to an already defined OneToMany
     * relationship, but using a different name. To get a ManyToOne relationship, define it
     * as its OneToMany equivalent then get its inverse.
     */
    data class AsymmetricManyToSingle<FROM, TO> internal constructor(
            override val name: String) :
            FromMany<FROM, TO>, ToSingle<FROM, TO>, Relationship.ManyToSingle<FROM, TO> {

        override val direction: Direction? get() = Direction.BACKWARD

        override val inverse: AsymmetricSingleToMany<TO, FROM>
            get() =
                AsymmetricSingleToMany(name = name)

        override val type: Type get() = Type.ASYMMETRIC_MANY_TO_SINGLE
    }

    data class AsymmetricManyToMany<FROM, TO>(
            override val name: String,
            override val direction: Direction = Direction.FORWARD) :
            FromMany<FROM, TO>, ToMany<FROM, TO>, Relationship.ManyToMany<FROM, TO> {

        override val inverse: AsymmetricManyToMany<TO, FROM>
            get() =
                AsymmetricManyToMany(name = name, direction = direction.inverse)

        override val type: Type get() = Type.ASYMMETRIC_MANY_TO_MANY
    }

    data class SymmetricManyToMany<TYPE>(
            override val name: String) :
            FromMany<TYPE, TYPE>, ToMany<TYPE, TYPE>, Relationship.ManyToMany<TYPE, TYPE> {

        override val direction: Direction? get() = null

        override val inverse: SymmetricManyToMany<TYPE> get() = this

        override val type: Type get() = Type.SYMMETRIC_MANY_TO_MANY
    }
}
