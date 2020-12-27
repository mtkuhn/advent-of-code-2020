package day24

import java.io.File

fun main() {
    //test()
    part1()
    part2()
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

fun part2() {
    var blackTiles = File("src/main/resources/day24_input.txt").readLines()
            .asSequence()
            .map { it.stringToDirections() }
            .map { it.consolidateDirections() }
            .groupBy { it }
            .map { it.key to it.value.size }
            .filter { it.second%2 == 1 }
            .map { it.first }
            .toSet()

    repeat(100) {
        blackTiles = blackTiles.iterateTileRules()
    }
    println(blackTiles.size)
}

fun Set<Pair<Int, Int>>.iterateTileRules(): Set<Pair<Int, Int>> {
    val adjacents = this.map { tile ->
        tile to tile.getAdjacentTiles()
    }.toMap()

    //Any black tile with zero or more than 2 black tiles immediately adjacent to it is flipped to white.
    val blackToWhite = adjacents.filter { (it.value intersect this).size != 1 }.map { it.key }

    //Any white tile with exactly 2 black tiles immediately adjacent to it is flipped to black.
    val whiteToBlack = adjacents.flatMap { it.value }.toSet().filter { tile ->
        (tile.getAdjacentTiles() intersect this).size == 2
    }

    return this - blackToWhite + whiteToBlack
}

fun Pair<Int, Int>.getAdjacentTiles(): List<Pair<Int, Int>> =
        Direction.values().map { dir -> this + dir.offset }

fun List<Direction>.consolidateDirections(): Pair<Int, Int> =
        this.map { dir -> dir.offset }.fold(0 to 0) { acc, element -> acc + element }

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

operator fun Pair<Int, Int>.plus(pos: Pair<Int, Int>) = first+pos.first to second+pos.second

enum class Direction(val offset: Pair<Int, Int>) {
    NE(1 to 2),
    NW(-1 to 2),
    SE(1 to -2),
    SW(-1 to -2),
    E(2 to 0),
    W(-2 to 0)
}