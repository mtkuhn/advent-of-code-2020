package day19

import java.io.File

fun main() {
    part1()
    part2()
}

fun part1() {
    val rules = getRuleMap().toMutableMap()

    while(rules.values.any { it.contains("\\d".toRegex()) }) {
        rules.forEach() { ruleMapEntry ->
            "\\(\\d+\\)".toRegex().findAll(ruleMapEntry.value)
                    .map { it.value }
                    .forEach { foundNumber ->
                        val intNumber = foundNumber.replace("[^\\d]".toRegex(), "").toInt()
                        val replacement = rules[intNumber]!!.drop(1).dropLast(1)
                        rules[ruleMapEntry.key] = ruleMapEntry.value.replace(foundNumber, "($replacement)")
                    }
        }
    }

    countMatchingMessages(rules).apply{ println(this) }
}

fun part2() {
    val rules = getRuleMap().toMutableMap()
    rules[8] = "((42)+)" //353

    var rule11 = (1..100).map {
        "((42){$it}(31){$it})"
    }.joinToString(separator = "|", prefix="(", postfix= ")")
    rules[11] = rule11

    while(rules.values.any { it.contains("\\(\\d+\\)".toRegex()) }) {
        rules.forEach() { ruleMapEntry ->
            "\\(\\d+\\)".toRegex().findAll(ruleMapEntry.value)
                    .map { it.value }
                    .forEach { foundNumber ->
                        val intNumber = foundNumber.replace("[()]".toRegex(), "").toInt()
                        val replacement = rules[intNumber]!!.drop(1).dropLast(1)
                        rules[ruleMapEntry.key] = ruleMapEntry.value.replace(foundNumber, "($replacement)")
                    }
        }
    }

    countMatchingMessages(rules.filter { it.key == 0 }).apply{ println(this) }
}

fun getRuleMap(): Map<Int, String> =
        File("src/main/resources/day19_rules.txt").readLines()
                .map { it.parseRule() }
                .toMap()

fun String.parseRule(): Pair<Int, String> {
    val split = this.split(": ")
    var id = split[0].toInt()
    var options = split[1].split(" | ").joinToString(separator = "|") { s ->
        s.split(" ").joinToString(separator = "", prefix = "(", postfix = ")") { n ->
            "(${n.replace("\"", "")})"
        }
    }
    options = "\\(\\([a-zA-Z]+\\)\\)".toRegex().replace(options) { it.value.drop(2).dropLast(2) }
    return id to "($options)"
}


fun countMatchingMessages(ruleMap: Map<Int, String>): Int =
        File("src/main/resources/day19_input.txt").readLines()
                .filter { line ->
                    ruleMap.any { rule -> rule.value.toRegex().matches(line) }
                }
                .count()