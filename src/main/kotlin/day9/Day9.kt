package day9

import java.io.File

fun main() {
    part2()
}

fun part1() {
    File("src/main/resources/day9_input.txt").readLines()//.asSequence()
            .map { it.toLong() }
            .rollingSlicePairedToFinal(26)
            .firstOrNull { pair ->
                pair.second.map { i -> pair.first-i }
                        .none() { pair.second.contains(it) }
            }
            .apply { println(this?.first) } //776203571
}

fun part2() {
    val goal: Long = 776203571
    val consecutiveRunningTallies = File("src/main/resources/day9_input.txt").readLines()
            .map { it.toLong() }
            .map { RunningTally(it, mutableListOf(it)) }

    var answer: RunningTally? = null
    while(answer == null) {
        answer = consecutiveRunningTallies.apply{ iterateRunningTally() }
                .firstOrNull { it.total == goal }
    }
    println(answer.sumOfMinAndMax())
}

data class RunningTally(var total: Long, private val values: MutableList<Long>) {
    operator fun plusAssign(element: Long) {
        this.values += element
        total += element
    }

    fun first(): Long = values.first()
    fun size(): Int = values.size
    fun sumOfMinAndMax(): Long = (values.minOrNull()?:0) + (values.maxOrNull()?:0)
}

fun List<RunningTally>.iterateRunningTally() =
    this.forEachIndexed { idx, element ->
        if (idx + element.size() < this.size) element += this[idx + element.size()].first()
    }

fun <T> List<T>.rollingSlicePairedToFinal(amount: Int): List<Pair<T, List<T>>> =
    this.foldIndexed(mutableListOf<Pair<T, List<T>>>()) { idx, acc, _ ->
        if(idx-amount+1 >= 0) acc += this[idx] to this.slice(idx-amount+1 until idx)
        acc
    }.toList()