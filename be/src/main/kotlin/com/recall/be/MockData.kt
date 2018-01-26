package com.recall.be

import com.recall.be.datamodel.*
import com.syncleus.ferma.FramedGraph
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component


@Component
class ApplicationReadyListener(val fg: FramedGraph): ApplicationListener<ApplicationReadyEvent> {

    override fun onApplicationEvent(event: ApplicationReadyEvent?) {
//        GraphOfTheGodsFactory.loadWithoutMixedIndex(graph, true)

        val newHope = fg.addFramedVertex(Episode::class.java)
        newHope.setName("New Hope")

        val jedi = fg.addFramedVertex(Episode::class.java)
        jedi.setName("Return of the Jedi")

        val empire = fg.addFramedVertex(Episode::class.java)
        empire.setName("Empire Strikes Back")

        val lukeSkywalker = fg.addFramedVertex(Human::class.java)
        lukeSkywalker.setName("Luke Skywalker")
        lukeSkywalker.setHomePlanet("Tatooine")
        lukeSkywalker.setAppearsIn(newHope, jedi, empire)

        val darthVader = fg.addFramedVertex(Human::class.java)
        darthVader.setName("Darth Vader")
        darthVader.setHomePlanet("Tatooine")
        darthVader.setAppearsIn(newHope, jedi, empire)

        val hanSolo = fg.addFramedVertex(Human::class.java)
        hanSolo.setName("Han Solo")
        hanSolo.setAppearsIn(newHope, jedi, empire)

        val leiaOrgana = fg.addFramedVertex(Human::class.java)
        leiaOrgana.setName("Leia Organa")
        leiaOrgana.setHomePlanet("Alderaan")
        leiaOrgana.setAppearsIn(newHope, jedi, empire)

        val wilhuffTarkin = fg.addFramedVertex(Human::class.java)
        wilhuffTarkin.setName("Wilhuff Tarkin")
        wilhuffTarkin.setAppearsIn(newHope)

        val c3po = fg.addFramedVertex(Droid::class.java)
        c3po.setName("C-3PO")
        c3po.setAppearsIn(newHope, jedi, empire)
        c3po.setPrimaryFunction("Protocol")

        val aretoo = fg.addFramedVertex(Droid::class.java)
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

        fg.tx().commit()
        println("Committed Mock Data")
    }
}
