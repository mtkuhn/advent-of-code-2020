package day10

import java.io.File

fun main() {
    part2()
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

fun part2() {
    File("src/main/resources/day10_input.txt").readLines().asSequence()
            .map { it.toLong() }
            .let { it.plus(sequenceOf(0, (it.maxOrNull()?:0)+3)) }
            .sorted()
            .zipWithNext { a, b -> b-a } //differences between sorted numbers
            .fold(mutableListOf(1L)) { acc, n -> //convert the list of diffs into a count of consecutive numbers
                if(n == 3L) acc += 1L
                else acc[acc.lastIndex] += 1L
                acc
            }
            .map { pathsInConsecutiveSequenceOfLength(it) }
            .reduce { acc, n -> acc * n }
            .apply { println(this) }
}

fun pathsInConsecutiveSequenceOfLength(length: Long): Long =
        if(length <= 2) 1
        else (1..minOf(3, length-1)).map { pathsInConsecutiveSequenceOfLength(length-it) }.sum()