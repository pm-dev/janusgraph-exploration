package com.starwars

import com.framework.gremlin.insert
import com.starwars.character.Character
import com.starwars.character.addFriends
import com.starwars.character.setAppearsIn
import com.starwars.character.setFriends
import com.starwars.droid.Droid
import com.starwars.episode.Episode
import com.starwars.human.Human
import com.syncleus.ferma.DelegatingFramedGraph
import com.syncleus.ferma.FramedGraph
import org.janusgraph.core.JanusGraph
import org.springframework.stereotype.Component

@Component
class StarwarsGraph(graph: JanusGraph): DelegatingFramedGraph<JanusGraph>(
        graph,
        true,
        setOf(
                Character::class.java,
                Droid::class.java,
                Human::class.java,
                Episode::class.java)) {
    init {
        loadStarwars()
    }
}

private fun FramedGraph.loadStarwars() {
    val newHope = insert<Episode>()
    newHope.setName("New Hope")

    val jedi = insert<Episode>()
    jedi.setName("Return of the Jedi")

    val empire = insert<Episode>()
    empire.setName("Empire Strikes Back")

    val lukeSkywalker = insert<Human>()
    lukeSkywalker.setName("Luke Skywalker")
    lukeSkywalker.setHomePlanet("Tatooine")
    lukeSkywalker.setAppearsIn(newHope, jedi, empire)

    val darthVader = insert<Human>()
    darthVader.setName("Darth Vader")
    darthVader.setHomePlanet("Tatooine")
    darthVader.setAppearsIn(newHope, jedi, empire)

    val hanSolo = insert<Human>()
    hanSolo.setName("Han Solo")
    hanSolo.setAppearsIn(newHope, jedi, empire)

    val leiaOrgana = insert<Human>()
    leiaOrgana.setName("Leia Organa")
    leiaOrgana.setHomePlanet("Alderaan")
    leiaOrgana.setAppearsIn(newHope, jedi, empire)

    val wilhuffTarkin = insert<Human>()
    wilhuffTarkin.setName("Wilhuff Tarkin")
    wilhuffTarkin.setAppearsIn(newHope)

    val c3po = insert<Droid>()
    c3po.setName("C-3PO")
    c3po.setAppearsIn(newHope, jedi, empire)
    c3po.setPrimaryFunction("Protocol")

    val aretoo = insert<Droid>()
    aretoo.setName("R2-D2")
    aretoo.setAppearsIn(newHope, jedi, empire)
    aretoo.setPrimaryFunction("Astromech")

    lukeSkywalker.setFriends(hanSolo, leiaOrgana, c3po, aretoo)
    darthVader.setFriends(wilhuffTarkin)
    hanSolo.setFriends(lukeSkywalker, leiaOrgana, aretoo)
    leiaOrgana.setFriends(lukeSkywalker, hanSolo, c3po, aretoo)
    wilhuffTarkin.setFriends(darthVader)

    c3po.addFriends(lukeSkywalker, hanSolo, leiaOrgana, aretoo)
    aretoo.addFriends(lukeSkywalker, hanSolo, leiaOrgana)

    tx().commit()
    println("Loaded Starwars Data")
}
