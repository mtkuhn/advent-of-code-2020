package day19

import java.io.File

fun main() {
    part1()
}

fun part1() {
    val rules = getRules()
    while(rules.any { it.value is HigherLevelRule }) {
        rules.forEach {
            val rule = it.value
            if(rule is HigherLevelRule) rules[it.key] = rule.flattenRule(rules)
        }
    }
    val acceptableMessages = rules.values.flatMap { (it as ConstantRule).options }
    getMessages().filter { message ->
        acceptableMessages.contains(message)
    }.count().apply { println(this) }
}

fun getRules() = File("src/main/resources/day19_rules.txt").readLines()
        .map { it.toRule() }
        .associateBy { it.id }
        .toMutableMap()
fun getMessages() = File("src/main/resources/day19_input.txt").readLines()

open class Rule(val id: Int)
class HigherLevelRule(id: Int, val options: List<List<Int>>): Rule(id) {
    fun flattenRule(ruleMap: Map<Int, Rule>): Rule {
        return if(canFlattenToConstant(ruleMap)) {
            val newOptions = options.flatMap { optionList ->
                optionList.map { optionId ->
                    (ruleMap[optionId] as ConstantRule).options
                }.buildOptionStrings()
            }
            ConstantRule(id, newOptions)
        }
        else this
    }

    private fun List<List<String>>.buildOptionStrings(): List<String> =
            if(this.size == 1) this.first()
            else this.drop(1).buildOptionStrings().flatMap { enum ->
                this.first().map { opt ->
                    opt + enum
                }
            }

    private fun canFlattenToConstant(ruleMap: Map<Int, Rule>) = options.flatten().all { ruleMap[it] is ConstantRule }
}
class ConstantRule(id: Int, val options: List<String>): Rule(id)

fun String.toRule(): Rule {
    val split = split(": ")
    return if(split[1].contains("\"")) {
        ConstantRule(split[0].toInt(), listOf(split[1].replace("\"", "")))
    } else {
        HigherLevelRule(split[0].toInt(),
                split[1].split(" | ").map { option ->
                    option.split(" ").map { id -> id.toInt() } })
    }
}

fun Rule.print() {
    when (this) {
        is HigherLevelRule -> {
            println("$id; $options")
        }
        is ConstantRule -> {
            println("$id; $options")
        }
        else -> {
            error("Invalid rule type")
        }
    }
}