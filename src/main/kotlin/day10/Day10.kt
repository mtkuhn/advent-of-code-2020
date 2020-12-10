package day10

import java.io.File

fun main() {
    part1()
}

fun part1() {
    File("src/main/resources/day10_input.txt").readLines().asSequence()
            .map { it.toLong() }
            .let { it.plus(sequenceOf(0, (it.maxOrNull()?:0)+3)) }
            .sorted()
            .zipWithNext { a, b -> b-a }
            .groupBy { it }
            .apply { println((this[1]?.size?:0) * (this[3]?.size?:0)) }
}