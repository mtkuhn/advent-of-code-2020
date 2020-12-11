package day11

import java.io.File

fun main() {
    part1()
    part2()
}

fun part1() {
    val input = SeatingGrid(File("src/main/resources/day11_input.txt").readLines())
    input.runPart1()
    println(input.countOccupiedSeats()) //2346
}

fun part2() {
    val input = SeatingGrid(File("src/main/resources/day11_input.txt").readLines())
    input.runPart2()
    println(input.countOccupiedSeats()) //2111
}

operator fun Pair<Int, Int>.plus(pos: Pair<Int, Int>) = first+pos.first to second+pos.second

operator fun Pair<Int, Int>.times(scale: Int) = first*scale to second*scale

class SeatingGrid(private var data: List<String>) {

    private operator fun get(pos: Pair<Int, Int>): Char = data[pos.first][pos.second]

    private fun isInBounds(pos: Pair<Int, Int>): Boolean =
            pos.first in (data.indices) && pos.second in (data[0].indices)

    private fun getAdjacentChars(pos: Pair<Int, Int>) =
            directions.map { it+pos }.filter { isInBounds(it) }.map { this[it] }

    private fun getVisibleChars(pos: Pair<Int, Int>) =
            directions.map { slope -> getFirstCharVisibleInSlope(slope, pos) }

    private fun getFirstCharVisibleInSlope(slope: Pair<Int, Int>, start: Pair<Int, Int>): Char =
            (1..maxOf(data.size, data[0].length)).asSequence().map {
                val pos = start+(slope*it)
                if(isInBounds(pos)) this[pos] else '.'
            }.firstOrNull { it != '.' }?:'.'

    private fun runRulesAndReturnIsUpdated(
            occToEmptyThreshold: Int, getChars: (Pair<Int, Int>) -> Sequence<Char>): Boolean {
        var updated = false
        data = data.asSequence().mapIndexed { x, line ->
            line.asSequence().mapIndexed { y, char ->
                if (char == 'L' && !getChars(x to y).any { it == '#' }) {
                    updated = true
                    '#'
                }
                else if (char == '#' && getChars(x to y).count { it == '#' } >= occToEmptyThreshold) {
                    updated = true
                    'L'
                }
                else char
            }.joinToString(separator = "")
        }.toList()
        return updated
    }

    private fun iterateRulesToSteadyState(occToEmptyThreshold: Int, getChars: (Pair<Int, Int>) -> Sequence<Char>) {
        var updated = true
        while(updated) { updated = runRulesAndReturnIsUpdated(occToEmptyThreshold, getChars) }
    }

    fun countOccupiedSeats(): Int = data.map { line -> line.count { it == '#' } }.sum()

    fun runPart1() = iterateRulesToSteadyState(4) { pos -> getAdjacentChars(pos) }

    fun runPart2() = iterateRulesToSteadyState(5) { pos -> getVisibleChars(pos) }

    companion object {
        private val directions =
                sequenceOf((-1 to -1), (-1 to 0), (-1 to +1), (0 to -1), (0 to +1), (+1 to -1), (+1 to 0), (+1 to +1))
    }

}
