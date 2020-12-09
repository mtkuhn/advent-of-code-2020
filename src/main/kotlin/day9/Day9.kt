package day9

import java.io.File

fun main() {
    part1()
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

fun <T> List<T>.rollingSlicePairedToFinal(amount: Int): List<Pair<T, List<T>>> =
    this.foldIndexed(mutableListOf<Pair<T, List<T>>>()) { idx, acc, _ ->
        if(idx-amount+1 >= 0) acc += this[idx] to this.slice(idx-amount+1..idx-1)
        acc
    }.toList()