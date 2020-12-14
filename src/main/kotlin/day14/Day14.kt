package day14

import java.io.File

fun main() {
    part1()
}

fun getInput(): List<String> =
        File("src/main/resources/day14_input.txt").readLines()

fun part1() {
    var mask = 0L to 0L
    var memoryMap = mutableMapOf<Long, Long>()
    getInput().map { it.split(" = ") }
            .map { s ->
                if(s[0] == "mask") maskInstruction(s[1])
                else memInstruction(s[0], s[1])
            }
            .forEach { instruction ->
                if(instruction.address >=0) memoryMap[instruction.address] = maskValue(instruction.p1, mask)
                else mask = instruction.p1 to instruction.p2
            }

    memoryMap.values.sum().apply { println(this) }
}

data class Instruction(val address: Long, val p1: Long, val p2: Long)

fun maskInstruction(str: String) =
        (str.replace("X", "0").toLong(2) to str.replace("X", "1").toLong(2)) //and then or
            .let{ b -> Instruction(-1, b.first, b.second) }

fun memInstruction(assign: String, value: String) =
        Instruction(
                "mem\\[([0-9]+)\\]".toRegex().find(assign)?.groupValues?.get(1)?.toLong()?:-1,
                value.toLong(),
                0
        )

fun maskValue(value: Long, mask: Pair<Long, Long>) =
    value or mask.first and mask.second

//or for 1's, and for 0's