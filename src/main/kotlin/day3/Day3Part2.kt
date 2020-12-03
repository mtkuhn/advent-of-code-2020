package day3

import java.io.File

fun main() {

    val input = File("src/main/resources/day3_input.txt").readLines().asSequence()

    val slopesRightToDown = listOf((1 to 1), (3 to 1), (5 to 1), (7 to 1), (1 to 2))

    val treeCountProduct = slopesRightToDown
            .map { countTreesForSlope(it.first, it.second, input).toBigInteger() }
            .reduce { acc, count ->  acc * count }
    println(treeCountProduct)
}


fun countTreesForSlope(right: Int, down: Int, input: Sequence<String>) =
    input.filterIndexed { i, _ -> i%down == 0} //skip rows for the down amount
            .mapIndexed { i, row -> row[(i*right)%row.length] } //isolate the char at position, use mod to circle around
            .count { it == '#' }
