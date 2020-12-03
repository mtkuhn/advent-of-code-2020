package day3

import java.io.File

fun main() {
    val treeCount = File("src/main/resources/day3_input.txt").readLines()
            .map { line -> line.toCharArray() }
            .toTypedArray()
            .mapIndexed { i, row -> row[(i*3)%row.size] }
            .count { it == '#' }
    println("$treeCount trees encountered")
}