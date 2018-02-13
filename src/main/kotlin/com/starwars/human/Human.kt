package com.starwars.human

import com.starwars.character.Character
import com.syncleus.ferma.annotations.Property

abstract class Human: Character() {

    @Property("homePlanet")
    abstract fun getHomePlanet(): String?

    @Property("homePlanet")
    abstract fun setHomePlanet(homePlanet: String?)
}
