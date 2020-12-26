package day23

fun main() {
    part1()
    part2()
}

fun part1() {
    val cupMap: Map<Int, Cup> = ("792845136" + "0")
            .map {
                Cup(it.toString().toInt(), null)
            }
            .zipWithNext()
            .onEach { cup -> cup.first.next = cup.second }
            .apply { this.last().first.next = this.first().first }
            .map { it.first.label to it.first }
            .toMap()
    val cups = CupCircle2(cupMap, cupMap[7]!!)
    repeat(100) { cups.crabMove() }
    println(cups.cupsAfterOne(8))
} //98742365

fun part2() {

    val cupMap: Map<Int, Cup> = ("792845136".map { it.toString().toInt() } + (10 .. 1000000).toList() + listOf(0))
            .map {
                Cup(it.toString().toInt(), null)
            }
            .zipWithNext()
            .onEach { cup -> cup.first.next = cup.second }
            .apply { this.last().first.next = this.first().first }
            .map { it.first.label to it.first }
            .toMap()
    val cups = CupCircle2(cupMap, cupMap[7]!!)
    repeat(10000000) { cups.crabMove() }
    println(cups.cupsAfterOne(3))
} //294,320,513,093

data class Cup(val label: Int, var next: Cup?, var isPickedUp: Boolean = false) {
    fun next() = next!!
}

class CupCircle2(val labelCupMap: Map<Int, Cup>,
                 var currentCup: Cup,
                 private val maxLabel: Int = labelCupMap.maxOfOrNull{ it.key }?:0,
                 private val minLabel: Int = labelCupMap.minOfOrNull{ it.key }?:0) {

    fun removeNextCups(count: Int): List<Cup> = (0 until count).map {
        val nextCups = listOf(currentCup.next(), currentCup.next().next(), currentCup.next().next().next())
        nextCups.forEach { it.isPickedUp = true }
        currentCup.next = nextCups.last().next
        return nextCups
    }

    fun getNextCupAtOrBelow(targetLabel: Int): Cup {
        val label = if(targetLabel < minLabel) maxLabel else targetLabel
        return if(labelCupMap[label]!!.isPickedUp == true) getNextCupAtOrBelow(label-1)
        else labelCupMap[label]!!
    }

    fun addAllAfterCup(startingCup: Cup, newCups: List<Cup>) {
        newCups.last().next = startingCup.next
        startingCup.next = newCups.first()
        newCups.forEach { it.isPickedUp = false }
    }

    fun cupsAfterOne(count: Int): String {
        val cupOne = labelCupMap[1]!!
        val cupList = mutableListOf<Cup>(cupOne.next())
        (1 until count).forEach {
            cupList.add(cupList.last().next())
        }
        return cupList.map { it.label }.joinToString()
    }

    fun crabMove() {
        val pickUpCups = removeNextCups(3)
        //println("pick: $pickUpCups")
        val destinationCup = getNextCupAtOrBelow(currentCup.label-1)
        //println("dest=$destinationCupLabel")
        addAllAfterCup(destinationCup, pickUpCups)
        currentCup = currentCup.next()
    }
}