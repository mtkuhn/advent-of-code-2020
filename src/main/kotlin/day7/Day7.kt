package day7

import java.io.File

fun main() {
    part1()
}

fun part1() {
    File("src/main/resources/day7_input.txt").readLines()
            .map { BagRule.fromString(it) }
            .findContainingBagColors("shiny gold")
            .count()
}

fun List<BagRule>.findContainingBagColors(evalColor: String): Set<String> {
    val directColors = this.filter { r -> r.contents.map { p -> p.second }.contains(evalColor) }
            .map { it.color }
            .toSet()
    val indirectColors = directColors.flatMap { this.findContainingBagColors(it) }.toSet()
    return directColors + indirectColors
}

data class BagRule(val color: String, val contents: List<Pair<Int, String>>) {
    companion object {
        fun fromString(input: String): BagRule {
            val split = input.split(" bags contain ")
            val subjectColor = split[0]
            val containedBags = split[1].split(", ".toRegex()).toList()
                    .flatMap {
                        "(\\d+) (.+) bags?".toRegex().findAll(it).map { b ->
                            b.groupValues[1].toInt() to b.groupValues[2]
                        }
                    }
            return BagRule(subjectColor, containedBags)
        }
    }
}