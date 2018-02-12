package com.framework.datamodel.edges

interface Relationship<FROM, TO> {

    val inverse: Relationship<TO, FROM>

    val hops: List<Hop<*, *>>

    fun <NEXT> to(next: ManyToMany<TO, NEXT>): ManyToMany<FROM, NEXT> =
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
}
