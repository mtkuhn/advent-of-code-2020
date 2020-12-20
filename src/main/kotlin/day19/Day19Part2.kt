package day19

import java.io.File

fun main() {
    part1b()
    part2b()
}

fun part1b() {
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

fun part2b() {
    val rules = getRuleMap().toMutableMap()
    rules[8] = "8: 42 | 42 8".parseRule().second
    rules[11] = "11: 42 31 | 42 11 31".parseRule().second

    rules[8] = "((42)+)"
    rules[11] = "((42)+(31)+)"

    //rules.forEach {println(it)}

    while(rules.values.any { it.contains("\\d".toRegex()) }) {
        rules.forEach() { ruleMapEntry ->
            "\\(\\d+\\)".toRegex().findAll(ruleMapEntry.value)
                    .map { it.value }
                    .forEach { foundNumber ->
                        val intNumber = foundNumber.replace("[^\\d]".toRegex(), "").toInt()
                        if(intNumber != ruleMapEntry.key) {
                            val replacement = rules[intNumber]!!.drop(1).dropLast(1)
                            rules[ruleMapEntry.key] = ruleMapEntry.value.replace(foundNumber, "($replacement)")
                        } else {
                            val before = ruleMapEntry.value.substringBefore(foundNumber)
                            val after = ruleMapEntry.value.substringAfter(foundNumber)
                            val replacement = "($before$after)+"
                            println("rule= ${ruleMapEntry.value}")
                            println("repl= $replacement")
                            println("repd= ${ruleMapEntry.value.replace(foundNumber, replacement)}")
                            rules[ruleMapEntry.key] = "("+ruleMapEntry.value.replace(foundNumber, "")+"+)"
                        }
                    }

            //8: 42 | 42 8 -> (42)|((42)(8)) -> (42)|((42))+
            //11: 42 31 | 42 11 31 -> ((42)(31))|((42)(11)(31)) -> ((42)(31))|((42)(31))+
        }
    }

    println("rules iterated")

    //rules.forEach {println(it)}

    countMatchingMessages(rules).apply{ println(this) }
    //470 -- too high
    //458 -- wrong
    //418 -- too low
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