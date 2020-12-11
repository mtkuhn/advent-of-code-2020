package day11

import java.io.File

fun main() {
    part1()
    part2()
}

fun part1() {
    val input = Grid(File("src/main/resources/day11_input.txt").readLines())
    input.runPart1()
    println(input.countOccupiedSeats()) //2346
}

fun part2() {
    val input = Grid(File("src/main/resources/day11_input.txt").readLines())
    input.runPart2()
    println(input.countOccupiedSeats()) //2111
}

class Grid(private var data: List<String>) {

    private fun isInBounds(x: Int, y: Int): Boolean = x in (data.indices) && y in (data[0].indices)

    private fun getAdjacentChars(x: Int, y: Int) =
            sequenceOf((x-1 to y-1), (x-1 to y), (x-1 to y+1), (x to y-1), (x to y+1), (x+1 to y-1), (x+1 to y), (x+1 to y+1))
                    .filter { isInBounds(it.first, it.second) }
                    .map { data[it.first][it.second] }

    private fun getVisibleChars(x: Int, y: Int) =
            sequenceOf((-1 to -1), (-1 to 0), (-1 to +1), (0 to -1), (0 to +1), (+1 to -1), (+1 to 0), (+1 to +1))
                    .map { slope -> getFirstCharVisibleInSlope(slope.first, slope.second, x, y) }

    private fun getFirstCharVisibleInSlope(rise: Int, run: Int, x: Int, y: Int): Char =
            (1..maxOf(data.size, data[0].length)).asSequence().map {
                val x1 = x+(rise*it)
                val y1 = y+(run*it)
                if(isInBounds(x1, y1)) data[x1][y1] else '.'
            }.firstOrNull { it != '.' }?:'.'

    private fun iterateRulesToSteadyState(occToEmptyThreshold: Int, getChars: (Int, Int) -> Sequence<Char>) {
        var updated = true
        while(updated) { updated = runRulesAndReturnIsUpdated(occToEmptyThreshold, getChars) }
    }

    private fun runRulesAndReturnIsUpdated(occToEmptyThreshold: Int, getChars: (Int, Int) -> Sequence<Char>): Boolean {
        var updated = false
        data = data.asSequence().mapIndexed { x, line ->
            line.asSequence().mapIndexed { y, char ->
                if (char == 'L' && !getChars(x, y).any { it == '#' }) {
                    updated = true
                    '#'
                }
                else if (char == '#' && getChars(x, y).count { it == '#' } >= occToEmptyThreshold) {
                    updated = true
                    'L'
                }
                else char
            }.joinToString(separator = "")
        }.toList()
        return updated
    }

    fun countOccupiedSeats(): Int = data.map { line -> line.count { it == '#' } }.sum()

    fun runPart1() = iterateRulesToSteadyState(4) { x, y -> getAdjacentChars(x, y) }

    fun runPart2() = iterateRulesToSteadyState(5) { x, y -> getVisibleChars(x, y) }

}
