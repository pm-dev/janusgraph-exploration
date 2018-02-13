package com.starwars.droid

import com.starwars.character.Character
import com.syncleus.ferma.annotations.Property

abstract class Droid: Character() {

    @Property("primaryFunction")
    abstract fun getPrimaryFunction(): String

    @Property("primaryFunction")
    abstract fun setPrimaryFunction(primaryFunction: String)
}
