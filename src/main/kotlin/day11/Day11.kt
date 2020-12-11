package day11

import java.io.File

fun main() {
    part2()
}

fun part1() {
    val input = Grid(File("src/main/resources/day11_input.txt").readLines())

    var updated = true
    while(updated) {
        updated = input.runRulesAndReturnIsUpdated()
    }
    println(input.countOccupiedSeats()) //2346
}

fun part2() {
    val input = Grid(File("src/main/resources/day11_input.txt").readLines())

    var updated = true
    while(updated) {
        updated = input.runPart2RulesAndReturnIsUpdated()
    }
    println(input.countOccupiedSeats())
} //2111

class Grid(private var data: List<String>) {

    private fun isInBounds(x: Int, y: Int): Boolean =
        x >= 0 && y >= 0  && x < data.size && y < data[0].length

    private fun getAdjacentChars(x: Int, y: Int) =
            listOf((x-1 to y-1), (x-1 to y), (x-1 to y+1), (x to y-1), (x to y+1), (x+1 to y-1), (x+1 to y), (x+1 to y+1))
                    .filter { isInBounds(it.first, it.second) }
                    .map { data[x][y] }

    private fun getVisibleChars(x: Int, y: Int) =
            listOf((-1 to -1), (-1 to 0), (-1 to +1), (0 to -1), (0 to +1), (+1 to -1), (+1 to 0), (+1 to +1))
                    .map { slope ->
                        getFirstCharVisibleInSlope(slope.first, slope.second, x, y)
                    }

    private fun getFirstCharVisibleInSlope(rise: Int, run: Int, x: Int, y: Int): Char {
        var i = 1
        var c = '.'
        while(c == '.') {
            val x1 = x+(rise*i)
            val y1 = y+(run*i)
            c = if(isInBounds(x1, y1)) data[x1][y1] else '?'
            i += 1
        }
        return if(c == '?') '.' else c
    }

    fun runRulesAndReturnIsUpdated(): Boolean {
        var updated = false
        data = data.mapIndexed { x, line ->
            line.asSequence().mapIndexed { y, char ->
                if (char == 'L' && !getAdjacentChars(x, y).any { it == '#' }) {
                    updated = true
                    '#'
                }
                else if (char == '#' && getAdjacentChars(x, y).count { it == '#' } >= 4) {
                    updated = true
                    'L'
                }
                else char
            }.joinToString(separator = "")
        }.toList()
        return updated
    }

    fun runPart2RulesAndReturnIsUpdated(): Boolean {
        var updated = false
        data = data.asSequence().mapIndexed { x, line ->
            line.asSequence().mapIndexed { y, char ->
                if (char == 'L' && !getVisibleChars(x, y).any { it == '#' }) {
                    updated = true
                    '#'
                }
                else if (char == '#' && getVisibleChars(x, y).count { it == '#' } >= 5) {
                    updated = true
                    'L'
                }
                else char
            }.joinToString(separator = "")
        }.toList()
        return updated
    }

    fun countOccupiedSeats(): Int =
        data.map { line -> line.count { it == '#' } }.sum()

}
