package com.recall.be.datamodel

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
    val newHope = addFramedVertex(Episode::class.java)
    newHope.setName("New Hope")

    val jedi = addFramedVertex(Episode::class.java)
    jedi.setName("Return of the Jedi")

    val empire = addFramedVertex(Episode::class.java)
    empire.setName("Empire Strikes Back")

    val lukeSkywalker = addFramedVertex(Human::class.java)
    lukeSkywalker.setName("Luke Skywalker")
    lukeSkywalker.setHomePlanet("Tatooine")
    lukeSkywalker.setAppearsIn(newHope, jedi, empire)

    val darthVader = addFramedVertex(Human::class.java)
    darthVader.setName("Darth Vader")
    darthVader.setHomePlanet("Tatooine")
    darthVader.setAppearsIn(newHope, jedi, empire)

    val hanSolo = addFramedVertex(Human::class.java)
    hanSolo.setName("Han Solo")
    hanSolo.setAppearsIn(newHope, jedi, empire)

    val leiaOrgana = addFramedVertex(Human::class.java)
    leiaOrgana.setName("Leia Organa")
    leiaOrgana.setHomePlanet("Alderaan")
    leiaOrgana.setAppearsIn(newHope, jedi, empire)

    val wilhuffTarkin = addFramedVertex(Human::class.java)
    wilhuffTarkin.setName("Wilhuff Tarkin")
    wilhuffTarkin.setAppearsIn(newHope)

    val c3po = addFramedVertex(Droid::class.java)
    c3po.setName("C-3PO")
    c3po.setAppearsIn(newHope, jedi, empire)
    c3po.setPrimaryFunction("Protocol")

    val aretoo = addFramedVertex(Droid::class.java)
    aretoo.setName("R2-D2")
    aretoo.setAppearsIn(newHope, jedi, empire)
    aretoo.setPrimaryFunction("Astromech")

    lukeSkywalker.addFriends(hanSolo, leiaOrgana, c3po, aretoo)
    darthVader.addFriends(wilhuffTarkin)
    hanSolo.addFriends(lukeSkywalker, leiaOrgana, aretoo)
    leiaOrgana.addFriends(lukeSkywalker, hanSolo, c3po, aretoo)
    wilhuffTarkin.addFriends(darthVader)

    c3po.addFriends(lukeSkywalker, hanSolo, leiaOrgana, aretoo)
    aretoo.addFriends(lukeSkywalker, hanSolo, leiaOrgana)

    tx().commit()
    println("Loaded Starwars Data")
}
