package day24

import java.io.File

fun main() {
    //test()
    part1()
}

fun part1() {
    File("src/main/resources/day24_input.txt").readLines()
            .asSequence()
            .map { it.stringToDirections() }
            .map { it.consolidateDirections() }
            .groupBy { it }
            .map { it.key to it.value.size }
            .filter { it.second%2 == 1 }
            .count()
            .apply { println(this) }
} //232

fun List<Direction>.consolidateDirections(): Pair<Int, Int> {
    return this.map { dir ->
        when(dir) { //east to north
            Direction.NE -> 1 to 2
            Direction.NW -> -1 to 2
            Direction.SE -> 1 to -2
            Direction.SW -> -1 to -2
            Direction.E  -> 2 to 0
            Direction.W  -> -2 to 0
        }
    }.fold(0 to 0) { acc, element -> acc.first+element.first to acc.second+element.second }
}

fun String.stringToDirections(): List<Direction> {
    val directions = mutableListOf<Direction>()
    var str = this
    while(str.isNotEmpty()) {
        directions += str.getFirstDirection()
        str = str.drop(directions.last().toString().length)
    }
    return directions
}

fun String.getFirstDirection() =
        this.take(2).let {
            when {
                it == "ne" -> Direction.NE
                it == "nw" -> Direction.NW
                it == "se" -> Direction.SE
                it == "sw" -> Direction.SW
                it.startsWith("e") -> Direction.E
                it.startsWith("w") -> Direction.W
                else -> error("Invalid direction character $it")
            }
        }

enum class Direction { NE, NW, SE, SW, E, W }