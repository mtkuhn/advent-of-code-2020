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
            .apply { println(this?.first) }
}

fun part2() {
    val goal: Long = 776203571
    File("src/main/resources/day9_input.txt").readLines()
            .map { it.toLong() }
            .map { RunningTally(it, mutableListOf(it)) }
            .apply { iterateRunningTally(goal) }
            .firstOrNull { it.total == goal }
            .apply { println(this?.sumOfMinAndMax()) }
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

fun List<RunningTally>.iterateRunningTally(goal: Long) =
    indices.takeWhile { _ ->
        apply {
            forEachIndexed { idx, element ->
                if (idx + element.size() < size) element += this[idx + element.size()].first()
            }
        }.none { it.total == goal }
    }

fun <T> List<T>.rollingSlicePairedToFinal(amount: Int): List<Pair<T, List<T>>> =
    foldIndexed(mutableListOf<Pair<T, List<T>>>()) { idx, acc, _ ->
        if(idx-amount+1 >= 0) acc += this[idx] to slice(idx-amount+1 until idx)
        acc
    }.toList()