package day11

import java.io.File

fun main() {
    part1()
}

fun part1() {
    val input = Grid(File("src/main/resources/day11_input.txt").readLines())

    var updated = true
    while(updated) {
        updated = input.runRulesAndReturnIsUpdated()
    }
    println(input.countOccupiedSeats())
}

class Grid(private var data: List<String>) {

    private fun getAdjacentCoordinates(x: Int, y: Int) =
            listOf((x-1 to y-1), (x-1 to y), (x-1 to y+1), (x to y-1), (x to y+1), (x+1 to y-1), (x+1 to y), (x+1 to y+1))
                    .filter { it.first >= 0 && it.second >= 0  && it.first < data.size && it.second < data[0].length }

    private fun allAdjacentUnoccupied(x: Int, y: Int): Boolean =
            !getAdjacentCoordinates(x, y)
                    .any { data[it.first][it.second] == '#' }


    private fun atLeastThisManyAdjacentOccupied(min: Int, x: Int, y: Int): Boolean =
            getAdjacentCoordinates(x, y)
                    .count { data[it.first][it.second] == '#' } >= min

    fun runRulesAndReturnIsUpdated(): Boolean {
        var updated = false
        data = data.mapIndexed { x, line ->
            line.mapIndexed { y, char ->
                if (char == 'L' && allAdjacentUnoccupied(x, y)) {
                    updated = true
                    '#'
                }
                else if (char == '#' && atLeastThisManyAdjacentOccupied(4, x, y)) {
                    updated = true
                    'L'
                }
                else char
            }.joinToString(separator = "")
        }
        return updated
    }

    fun countOccupiedSeats(): Int =
        data.map { line -> line.count { it == '#' } }.sum()

    fun print() {
        data.onEach { println(it) }
    }

}
