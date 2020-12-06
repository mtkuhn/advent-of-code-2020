package day6

import java.io.File

fun main() {
    part1()
}

fun part1() {
    File("src/main/resources/day6_input.txt").readLines()
            .foldToBlankLineSeparated()
            .map { group -> group.filter { it.isLetter() } }
            .map { it.toSet().size }
            .sum()
            .apply { println(this) }
}

fun List<String>.foldToBlankLineSeparated(): List<String> =
    this.fold(mutableListOf("")) { list, line  ->
        if(line.isBlank()) list.add("")
        else list[list.lastIndex] += (if(list.last().isBlank()) "" else " ")+line
        list
    }