package day6

import java.io.File

fun main() {
    part2()
}

fun part1() {
    File("src/main/resources/day6_input.txt").readLines()
            .foldToBlankLineSeparated()
            .map { group -> group.filter { it.isLetter() } }
            .map { it.toSet().size }
            .sum()
            .apply { println(this) }
}

fun part2() {
    File("src/main/resources/day6_input.txt").readLines()
            .foldToBlankLineSeparated("\n")
            .map { group -> group.split("\n").intersectAll() }
            .map { it.toSet().size }
            .sum()
            .apply { println(this) }
}

fun List<String>.foldToBlankLineSeparated(separator: String = " "): List<String> =
    this.fold(mutableListOf("")) { list, line  ->
        if(line.isBlank()) list.add("")
        else list[list.lastIndex] += (if(list.last().isBlank()) "" else separator)+line
        list
    }

fun List<String>.intersectAll(): Set<Char> =
        this.map { it.toSet() }.reduce { acc, element -> acc intersect element }