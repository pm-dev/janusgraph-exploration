package com.recall.be.relationships


internal interface Linked<FROM, TO>: Relationship<FROM, TO> {

    override val inverse: Linked<TO, FROM>

    override val hops: List<Hop<*, *>> get() = first.hops + last.hops

    val first: Relationship<FROM, *>
    val last: Relationship<*, TO>

    data class OptionalToOptional<FROM, TO>(
            override val first: Relationship.OneToOne<FROM, *>,
            override val last: Relationship.OneToOne<*, TO>):
            Linked<FROM, TO>, Relationship.OptionalToOptional<FROM, TO> {

        override val inverse: OptionalToOptional<TO, FROM> get() =
            OptionalToOptional(first = last.inverse, last = first.inverse)
    }

    data class OptionalToSingle<FROM, TO>(
            override val first: Relationship.OneToSingle<FROM, *>,
            override val last: Relationship.OneToSingle<*, TO>):
            Linked<FROM, TO>, Relationship.OptionalToSingle<FROM, TO> {

        override val inverse: SingleToOptional<TO, FROM> get() =
            SingleToOptional(first = last.inverse, last = first.inverse)
    }

    data class SingleToOptional<FROM, TO>(
            override val first: Relationship.SingleToOne<FROM, *>,
            override val last: Relationship.SingleToOne<*, TO>):
            Linked<FROM, TO>, Relationship.SingleToOptional<FROM, TO> {

        override val inverse: OptionalToSingle<TO, FROM> get() =
            OptionalToSingle(first = last.inverse, last = first.inverse)
    }

    data class SingleToSingle<FROM, TO>(
            override val first: Relationship.SingleToSingle<FROM, *>,
            override val last: Relationship.SingleToSingle<*, TO>):
            Linked<FROM, TO>, Relationship.SingleToSingle<FROM, TO> {

        override val inverse: SingleToSingle<TO, FROM> get() =
            SingleToSingle(first = last.inverse, last = first.inverse)
    }

    data class OptionalToMany<FROM, TO>(
            override val first: Relationship.FromOne<FROM, *>,
            override val last: Relationship.FromOne<*, TO>):
            Linked<FROM, TO>, Relationship.OptionalToMany<FROM, TO> {

        override val inverse: ManyToOptional<TO, FROM> get() =
            ManyToOptional(first = last.inverse, last = first.inverse)
    }

    data class SingleToMany<FROM, TO>(
            override val first: Relationship.FromSingle<FROM, *>,
            override val last: Relationship.FromSingle<*, TO>):
            Linked<FROM, TO>, Relationship.SingleToMany<FROM, TO> {

        override val inverse: ManyToSingle<TO, FROM> get() =
            ManyToSingle(first = last.inverse, last = first.inverse)
    }

    data class ManyToOptional<FROM, TO>(
            override val first: Relationship.ToOne<FROM, *>,
            override val last: Relationship.ToOne<*, TO>):
            Linked<FROM, TO>, Relationship.ManyToOptional<FROM, TO> {

        override val inverse: OptionalToMany<TO, FROM> get() =
            OptionalToMany(first = last.inverse, last = first.inverse)
    }

    data class ManyToSingle<FROM, TO>(
            override val first: Relationship.ToSingle<FROM, *>,
            override val last: Relationship.ToSingle<*, TO>):
            Linked<FROM, TO>, Relationship.ManyToSingle<FROM, TO> {

        override val inverse: SingleToMany<TO, FROM> get() =
            SingleToMany(first = last.inverse, last = first.inverse)
    }

    data class ManyToMany<FROM, TO>(
            override val first: Relationship<FROM, *>,
            override val last: Relationship<*, TO>):
            Linked<FROM, TO>, Relationship.ManyToMany<FROM, TO> {

        override val inverse: ManyToMany<TO, FROM> get() =
            ManyToMany(first = last.inverse, last = first.inverse)
    }
}
