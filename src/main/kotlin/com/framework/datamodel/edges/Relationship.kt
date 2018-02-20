package com.framework.datamodel.edges

interface Relationship<FROM, TO> {

    val inverse: Relationship<TO, FROM>

    val hops: List<Hop<*, *>>

    fun <NEXT> to(
            next: ManyToMany<TO, NEXT>
    ): ManyToMany<FROM, NEXT> =
            Linked.ManyToMany(first = this, last = next)

    interface FromOne<FROM, TO>: Relationship<FROM, TO> {
        override val inverse: ToOne<TO, FROM>
    }

    interface FromOptional<FROM, TO>: FromOne<FROM, TO> {

        override val inverse: ToOptional<TO, FROM>

        fun <NEXT> to(next: OptionalToMany<TO, NEXT>): OptionalToMany<FROM, NEXT> =
                Linked.OptionalToMany(first = this, last = next)

        fun <NEXT> to(next: SingleToMany<TO, NEXT>): OptionalToMany<FROM, NEXT> =
                Linked.OptionalToMany(first = this, last = next)
    }

    interface FromSingle<FROM, TO>: FromOne<FROM, TO> {

        override val inverse: ToSingle<TO, FROM>

        fun <NEXT> to(next: OptionalToMany<TO, NEXT>): OptionalToMany<FROM, NEXT> =
                Linked.OptionalToMany(first = this, last = next)

        fun <NEXT> to(next: SingleToMany<TO, NEXT>): SingleToMany<FROM, NEXT> =
                Linked.SingleToMany(first = this, last = next)
    }


    interface FromMany<FROM, TO>: Relationship<FROM, TO> {

        override val inverse: ToMany<TO, FROM>

        fun <NEXT> to(next: OptionalToMany<TO, NEXT>): ManyToMany<FROM, NEXT> =
                Linked.ManyToMany(first = this, last = next)

        fun <NEXT> to(next: SingleToMany<TO, NEXT>): ManyToMany<FROM, NEXT> =
                Linked.ManyToMany(first = this, last = next)
    }

    interface ToOne<FROM, TO>: Relationship<FROM, TO> {
        override val inverse: FromOne<TO, FROM>
    }

    interface ToOptional<FROM, TO>: ToOne<FROM, TO> {

        override val inverse: FromOptional<TO, FROM>

        fun <NEXT> to(next: ManyToOptional<TO, NEXT>): ManyToOptional<FROM, NEXT> =
                Linked.ManyToOptional(first = this, last = next)

        fun <NEXT> to(next: ManyToSingle<TO, NEXT>): ManyToOptional<FROM, NEXT> =
                Linked.ManyToOptional(first = this, last = next)
    }

    interface ToSingle<FROM, TO>: ToOne<FROM, TO> {

        override val inverse: FromSingle<TO, FROM>

        fun <NEXT> to(next: ManyToOptional<TO, NEXT>): ManyToOptional<FROM, NEXT> =
                Linked.ManyToOptional(first = this, last = next)

        fun <NEXT> to(next: ManyToSingle<TO, NEXT>): ManyToSingle<FROM, NEXT> =
                Linked.ManyToSingle(first = this, last = next)
    }

    interface ToMany<FROM, TO>: Relationship<FROM, TO> {

        override val inverse: FromMany<TO, FROM>

        fun <NEXT> to(next: ManyToOptional<TO, NEXT>): ManyToMany<FROM, NEXT> =
                Linked.ManyToMany(first = this, last = next)

        fun <NEXT> to(next: ManyToSingle<TO, NEXT>): ManyToMany<FROM, NEXT> =
                Linked.ManyToMany(first = this, last = next)
    }

    interface OneToOne<FROM, TO>: FromOne<FROM, TO>, ToOne<FROM, TO> {
        override val inverse: OneToOne<TO, FROM>
    }

    interface OneToOptional<FROM, TO>: OneToOne<FROM, TO>, ToOptional<FROM, TO> {
        override val inverse: OptionalToOne<TO, FROM>
    }

    interface OneToSingle<FROM, TO>: OneToOne<FROM, TO>, ToSingle<FROM, TO> {
        override val inverse: SingleToOne<TO, FROM>
    }

    interface OptionalToOne<FROM, TO>: FromOptional<FROM, TO>, OneToOne<FROM, TO> {
        override val inverse: OneToOptional<TO, FROM>
    }

    interface SingleToOne<FROM, TO>: FromSingle<FROM, TO>, OneToOne<FROM, TO> {
        override val inverse: OneToSingle<TO, FROM>
    }

    interface OneToMany<FROM, TO>: FromOne<FROM, TO>, ToMany<FROM, TO> {
        override val inverse: ManyToOne<TO, FROM>
    }

    interface ManyToOne<FROM, TO>: FromMany<FROM, TO>, ToOne<FROM, TO> {
        override val inverse: OneToMany<TO, FROM>
    }

    interface OptionalToOptional<FROM, TO>: OptionalToOne<FROM, TO>, OneToOptional<FROM, TO> {

        override val inverse: OptionalToOptional<TO, FROM>

        fun <NEXT> to(next: OptionalToOptional<TO, NEXT>): OptionalToOptional<FROM, NEXT> =
                Linked.OptionalToOptional(first = this, last = next)

        fun <NEXT> to(next: OptionalToSingle<TO, NEXT>): OptionalToOptional<FROM, NEXT> =
                Linked.OptionalToOptional(first = this, last = next)

        fun <NEXT> to(next: SingleToOptional<TO, NEXT>): OptionalToOptional<FROM, NEXT> =
                Linked.OptionalToOptional(first = this, last = next)

        fun <NEXT> to(next: SingleToSingle<TO, NEXT>): OptionalToOptional<FROM, NEXT> =
                Linked.OptionalToOptional(first = this, last = next)
    }

    interface OptionalToSingle<FROM, TO>: OptionalToOne<FROM, TO>, OneToSingle<FROM, TO> {

        override val inverse: SingleToOptional<TO, FROM>

        fun <NEXT> to(next: OptionalToOptional<TO, NEXT>): OptionalToOptional<FROM, NEXT> =
                Linked.OptionalToOptional(first = this, last = next)

        fun <NEXT> to(next: OptionalToSingle<TO, NEXT>): OptionalToSingle<FROM, NEXT> =
                Linked.OptionalToSingle(first = this, last = next)

        fun <NEXT> to(next: SingleToOptional<TO, NEXT>): OptionalToOptional<FROM, NEXT> =
                Linked.OptionalToOptional(first = this, last = next)

        fun <NEXT> to(next: SingleToSingle<TO, NEXT>): OptionalToSingle<FROM, NEXT> =
                Linked.OptionalToSingle(first = this, last = next)
    }

    interface SingleToOptional<FROM, TO>: OneToOptional<FROM, TO>, SingleToOne<FROM, TO> {

        override val inverse: OptionalToSingle<TO, FROM>

        fun <NEXT> to(next: OptionalToOptional<TO, NEXT>): OptionalToOptional<FROM, NEXT> =
                Linked.OptionalToOptional(first = this, last = next)

        fun <NEXT> to(next: OptionalToSingle<TO, NEXT>): OptionalToOptional<FROM, NEXT> =
                Linked.OptionalToOptional(first = this, last = next)

        fun <NEXT> to(next: SingleToOptional<TO, NEXT>): SingleToOptional<FROM, NEXT> =
                Linked.SingleToOptional(first = this, last = next)

        fun <NEXT> to(next: SingleToSingle<TO, NEXT>): SingleToOptional<FROM, NEXT> =
                Linked.SingleToOptional(first = this, last = next)
    }

    interface SingleToSingle<FROM, TO>: OneToSingle<FROM, TO>, SingleToOne<FROM, TO> {

        override val inverse: SingleToSingle<TO, FROM>

        fun <NEXT> to(next: OptionalToOptional<TO, NEXT>): OptionalToOptional<FROM, NEXT> =
                Linked.OptionalToOptional(first = this, last = next)

        fun <NEXT> to(next: OptionalToSingle<TO, NEXT>): OptionalToSingle<FROM, NEXT> =
                Linked.OptionalToSingle(first = this, last = next)

        fun <NEXT> to(next: SingleToOptional<TO, NEXT>): SingleToOptional<FROM, NEXT> =
                Linked.SingleToOptional(first = this, last = next)

        fun <NEXT> to(next: SingleToSingle<TO, NEXT>): SingleToSingle<FROM, NEXT> =
                Linked.SingleToSingle(first = this, last = next)
    }

    interface OptionalToMany<FROM, TO>: FromOptional<FROM, TO>, OneToMany<FROM, TO> {

        override val inverse: ManyToOptional<TO, FROM>

        fun <NEXT> to(next: OptionalToOptional<TO, NEXT>): OptionalToMany<FROM, NEXT> =
                Linked.OptionalToMany(first = this, last = next)

        fun <NEXT> to(next: OptionalToSingle<TO, NEXT>): OptionalToMany<FROM, NEXT> =
                Linked.OptionalToMany(first = this, last = next)

        fun <NEXT> to(next: SingleToOptional<TO, NEXT>): OptionalToMany<FROM, NEXT> =
                Linked.OptionalToMany(first = this, last = next)

        fun <NEXT> to(next: SingleToSingle<TO, NEXT>): OptionalToMany<FROM, NEXT> =
                Linked.OptionalToMany(first = this, last = next)
    }

    interface SingleToMany<FROM, TO>: FromSingle<FROM, TO>, OneToMany<FROM, TO> {

        override val inverse: ManyToSingle<TO, FROM>

        fun <NEXT> to(next: OptionalToOptional<TO, NEXT>): OptionalToMany<FROM, NEXT> =
                Linked.OptionalToMany(first = this, last = next)

        fun <NEXT> to(next: OptionalToSingle<TO, NEXT>): OptionalToMany<FROM, NEXT> =
                Linked.OptionalToMany(first = this, last = next)

        fun <NEXT> to(next: SingleToOptional<TO, NEXT>): SingleToMany<FROM, NEXT> =
                Linked.SingleToMany(first = this, last = next)

        fun <NEXT> to(next: SingleToSingle<TO, NEXT>): SingleToMany<FROM, NEXT> =
                Linked.SingleToMany(first = this, last = next)
    }

    interface ManyToOptional<FROM, TO>: ToOptional<FROM, TO>, ManyToOne<FROM, TO> {

        override val inverse: OptionalToMany<TO, FROM>

        fun <NEXT> to(next: OptionalToOptional<TO, NEXT>): ManyToOptional<FROM, NEXT> =
                Linked.ManyToOptional(first = this, last = next)

        fun <NEXT> to(next: OptionalToSingle<TO, NEXT>): ManyToOptional<FROM, NEXT> =
                Linked.ManyToOptional(first = this, last = next)

        fun <NEXT> to(next: SingleToOptional<TO, NEXT>): ManyToOptional<FROM, NEXT> =
                Linked.ManyToOptional(first = this, last = next)

        fun <NEXT> to(next: SingleToSingle<TO, NEXT>): ManyToOptional<FROM, NEXT> =
                Linked.ManyToOptional(first = this, last = next)
    }

    interface ManyToSingle<FROM, TO>: ToSingle<FROM, TO>, ManyToOne<FROM, TO> {

        override val inverse: SingleToMany<TO, FROM>

        fun <NEXT> to(next: OptionalToOptional<TO, NEXT>): ManyToOptional<FROM, NEXT> =
                Linked.ManyToOptional(first = this, last = next)

        fun <NEXT> to(next: OptionalToSingle<TO, NEXT>): ManyToSingle<FROM, NEXT> =
                Linked.ManyToSingle(first = this, last = next)

        fun <NEXT> to(next: SingleToOptional<TO, NEXT>): ManyToOptional<FROM, NEXT> =
                Linked.ManyToOptional(first = this, last = next)

        fun <NEXT> to(next: SingleToSingle<TO, NEXT>): ManyToSingle<FROM, NEXT> =
                Linked.ManyToSingle(first = this, last = next)
    }

    interface ManyToMany<FROM, TO>: FromMany<FROM, TO>, ToMany<FROM, TO> {

        override val inverse: ManyToMany<TO, FROM>

        fun <NEXT> to(next: OptionalToOptional<TO, NEXT>): ManyToMany<FROM, NEXT> =
                Linked.ManyToMany(first = this, last = next)

        fun <NEXT> to(next: OptionalToSingle<TO, NEXT>): ManyToMany<FROM, NEXT> =
                Linked.ManyToMany(first = this, last = next)

        fun <NEXT> to(next: SingleToOptional<TO, NEXT>): ManyToMany<FROM, NEXT> =
                Linked.ManyToMany(first = this, last = next)

        fun <NEXT> to(next: SingleToSingle<TO, NEXT>): ManyToMany<FROM, NEXT> =
                Linked.ManyToMany(first = this, last = next)
    }

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
    }

    data class AsymmetricOptionalToOptional<FROM, TO>(
            override val name: String,
            override val direction: Relationship.Hop.Direction = Relationship.Hop.Direction.FORWARD
    ):
            Relationship.Hop.FromOptional<FROM, TO>,
            Relationship.Hop.ToOptional<FROM, TO>,
            Relationship.OptionalToOptional<FROM, TO> {

        override val inverse: AsymmetricOptionalToOptional<TO, FROM> get() =
            AsymmetricOptionalToOptional(name = name, direction = direction.inverse)

        override val type: Relationship.Hop.Type get() = Relationship.Hop.Type.ASYMMETRIC_OPTIONAL_TO_OPTIONAL
    }

    data class AsymmetricOptionalToSingle<FROM, TO>(
            override val name: String,
            override val direction: Relationship.Hop.Direction = Relationship.Hop.Direction.FORWARD
    ):
            Relationship.Hop.FromOptional<FROM, TO>,
            Relationship.Hop.ToSingle<FROM, TO>,
            Relationship.OptionalToSingle<FROM, TO> {

        override val inverse: AsymmetricSingleToOptional<TO, FROM> get() =
            AsymmetricSingleToOptional(name = name, direction = direction.inverse)

        override val type: Relationship.Hop.Type get() = Relationship.Hop.Type.ASYMMETRIC_OPTIONAL_TO_SINGLE
    }

    data class AsymmetricSingleToOptional<FROM, TO>(
            override val name: String,
            override val direction: Relationship.Hop.Direction = Relationship.Hop.Direction.FORWARD
    ):
            Relationship.Hop.FromSingle<FROM, TO>,
            Relationship.Hop.ToOptional<FROM, TO>,
            Relationship.SingleToOptional<FROM, TO> {

        override val inverse: AsymmetricOptionalToSingle<TO, FROM> get() =
            AsymmetricOptionalToSingle(name = name, direction = direction.inverse)

        override val type: Relationship.Hop.Type get() = Relationship.Hop.Type.ASYMMETRIC_SINGLE_TO_OPTIONAL
    }

    data class AsymmetricSingleToSingle<FROM, TO>(
            override val name: String,
            override val direction: Relationship.Hop.Direction = Relationship.Hop.Direction.FORWARD
    ):
            Relationship.Hop.FromSingle<FROM, TO>,
            Relationship.Hop.ToSingle<FROM, TO>,
            Relationship.SingleToSingle<FROM, TO> {

        override val inverse: AsymmetricSingleToSingle<TO, FROM> get() =
            AsymmetricSingleToSingle(name = name, direction = direction.inverse)

        override val type: Relationship.Hop.Type get() = Relationship.Hop.Type.ASYMMETRIC_SINGLE_TO_SINGLE
    }

    data class SymmetricOptionalToOptional<TYPE>(
            override val name: String
    ) :
            Relationship.Hop.SymmetricOneToOne<TYPE>,
            Relationship.Hop.FromOptional<TYPE, TYPE>,
            Relationship.Hop.ToOptional<TYPE, TYPE>,
            Relationship.OptionalToOptional<TYPE, TYPE> {

        override val direction: Relationship.Hop.Direction? get() = null

        override val inverse: SymmetricOptionalToOptional<TYPE> get() = this

        override val type: Relationship.Hop.Type get() = Relationship.Hop.Type.SYMMETRIC_OPTIONAL_TO_OPTIONAL
    }

    data class SymmetricSingleToSingle<TYPE>(
            override val name: String
    ) :
            Relationship.Hop.SymmetricOneToOne<TYPE>,
            Relationship.Hop.FromSingle<TYPE, TYPE>,
            Relationship.Hop.ToSingle<TYPE, TYPE>,
            Relationship.SingleToSingle<TYPE, TYPE> {

        override val direction: Relationship.Hop.Direction? get() = null

        override val inverse: SymmetricSingleToSingle<TYPE> get() = this

        override val type: Relationship.Hop.Type get() = Relationship.Hop.Type.SYMMETRIC_SINGLE_TO_SINGLE
    }

    data class AsymmetricSingleToMany<FROM, TO>(
            override val name: String
    ) :
            Relationship.Hop.FromSingle<FROM, TO>,
            Relationship.Hop.ToMany<FROM, TO>,
            Relationship.SingleToMany<FROM, TO> {

        override val direction: Relationship.Hop.Direction? get() = Relationship.Hop.Direction.FORWARD

        override val inverse: AsymmetricManyToSingle<TO, FROM> get() =
            AsymmetricManyToSingle(name = name)

        override val type: Relationship.Hop.Type get() = Relationship.Hop.Type.ASYMMETRIC_SINGLE_TO_MANY
    }

    data class AsymmetricOptionalToMany<FROM, TO>(
            override val name: String
    ) :
            Relationship.Hop.FromOptional<FROM, TO>,
            Relationship.Hop.ToMany<FROM, TO>,
            Relationship.OptionalToMany<FROM, TO> {

        override val direction: Relationship.Hop.Direction? get() = Relationship.Hop.Direction.FORWARD

        override val inverse: AsymmetricManyToOptional<TO, FROM> get() =
            AsymmetricManyToOptional(name = name)

        override val type: Relationship.Hop.Type get() = Relationship.Hop.Type.ASYMMETRIC_OPTIONAL_TO_MANY
    }

    /**
     * We restrict creating ManyToOne edge by clients to prevent creation of a
     * ManyToOne relationship that is equivalent in meaning to an already defined OneToMany
     * relationship, but using a different name. To get a ManyToOne relationship, define it
     * as its OneToMany equivalent then get its inverse.
     */
    data class AsymmetricManyToOptional<FROM, TO> internal constructor(
            override val name: String
    ) :
            Relationship.Hop.FromMany<FROM, TO>,
            Relationship.Hop.ToOptional<FROM, TO>,
            Relationship.ManyToOptional<FROM, TO> {

        override val direction: Relationship.Hop.Direction? get() = Relationship.Hop.Direction.BACKWARD

        override val inverse: AsymmetricOptionalToMany<TO, FROM> get() =
            AsymmetricOptionalToMany(name = name)

        override val type: Relationship.Hop.Type get() = Relationship.Hop.Type.ASYMMETRIC_MANY_TO_OPTIONAL
    }

    /**
     * We restrict creating ManyToOne edge by clients to prevent creation of a
     * ManyToOne relationship that is equivalent in meaning to an already defined OneToMany
     * relationship, but using a different name. To get a ManyToOne relationship, define it
     * as its OneToMany equivalent then get its inverse.
     */
    data class AsymmetricManyToSingle<FROM, TO> internal constructor(
            override val name: String
    ) :
            Relationship.Hop.FromMany<FROM, TO>,
            Relationship.Hop.ToSingle<FROM, TO>,
            Relationship.ManyToSingle<FROM, TO> {

        override val direction: Relationship.Hop.Direction? get() = Relationship.Hop.Direction.BACKWARD

        override val inverse: AsymmetricSingleToMany<TO, FROM> get() =
            AsymmetricSingleToMany(name = name)

        override val type: Relationship.Hop.Type get() = Relationship.Hop.Type.ASYMMETRIC_MANY_TO_SINGLE
    }

    data class AsymmetricManyToMany<FROM, TO>(
            override val name: String,
            override val direction: Relationship.Hop.Direction = Relationship.Hop.Direction.FORWARD
    ) :
            Relationship.Hop.FromMany<FROM, TO>,
            Relationship.Hop.ToMany<FROM, TO>,
            Relationship.ManyToMany<FROM, TO> {

        override val inverse: AsymmetricManyToMany<TO, FROM> get() =
            AsymmetricManyToMany(name = name, direction = direction.inverse)

        override val type: Relationship.Hop.Type get() = Relationship.Hop.Type.ASYMMETRIC_MANY_TO_MANY
    }

    data class SymmetricManyToMany<TYPE>(
            override val name: String
    ) :
            Relationship.Hop.FromMany<TYPE, TYPE>,
            Relationship.Hop.ToMany<TYPE, TYPE>,
            Relationship.ManyToMany<TYPE, TYPE> {

        override val direction: Relationship.Hop.Direction? get() = null

        override val inverse: SymmetricManyToMany<TYPE> get() = this

        override val type: Relationship.Hop.Type get() = Relationship.Hop.Type.SYMMETRIC_MANY_TO_MANY
    }
}
