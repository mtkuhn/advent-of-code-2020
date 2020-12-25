package day23

fun main() {
    test()
    part1()
}

fun part1() {
    val cups = CupCircle("792845136".map { it.toString().toInt() }.toMutableList())
    repeat(100) { move(cups) }
    println(cups.cupsAfterOne())
} //98742365

fun test() {
    val cups = CupCircle("389125467".map { it.toString().toInt() }.toMutableList())
    repeat(10) { move(cups) }
    println(cups.cupsAfterOne())
} //92658374

class CupCircle(private val list: MutableList<Int>, private var currentLabel: Int = list.first()) {
    fun currentLabel(): Int = currentLabel
    fun nextLabel() = list.elementAt((list.indexOf(currentLabel) + 1)%list.size)
    fun grabNextLabel() = nextLabel().apply { list.remove(this) }
    fun grabNextLabels(count: Int) = (0 until count).map { grabNextLabel() }
    fun getNextCupAtOrBelow(targetLabel: Int): Int {
        val label = if(targetLabel < list.minOrNull()?:0) (list.maxOrNull()?:0) else targetLabel
        return if(label in list) label
               else getNextCupAtOrBelow(label-1)
    }
    fun addAfterLabel(targetLabel: Int, newCups: List<Int>) {
        newCups.forEachIndexed { idx, cup ->
            list.add(list.indexOf(targetLabel)+idx+1, cup)
        }
    }
    fun advanceCurrentLabel() { currentLabel = nextLabel() }
    fun print() {
        println("curr=$currentLabel; cups=$list")
    }
    fun cupsAfterOne(): String {
        val i = list.indexOf(1)
        return (list.slice(i+1 .. list.lastIndex)
                + list.slice(0 until i))
                .joinToString(separator="")
    }
}

fun move(cups: CupCircle) {
    val pickUpCups = cups.grabNextLabels(3)
    //println("pick: $pickUpCups")

    val destinationCupLabel = cups.getNextCupAtOrBelow(cups.currentLabel()-1)
    //println("dest=$destinationCupLabel")

    cups.addAfterLabel(destinationCupLabel, pickUpCups)

    cups.advanceCurrentLabel()
}