package day14

import java.io.File
import kotlin.math.pow

fun main() {
    part1()
    part2()
}

fun getInput(): List<String> =
        File("src/main/resources/day14_input.txt").readLines()

fun part1() {
    var mask = 0L to 0L
    val memoryMap = mutableMapOf<Long, Long>()
    getInput().map { it.split(" = ") }
            .forEach { s ->
                if(s[0] == "mask") mask = (s[1].replace("X", "0").toLong(2)
                        to s[1].replace("X", "1").toLong(2))
                else memoryMap[getMemoryInstructionPosition(s[0])] =
                        s[1].toLong() or mask.first and mask.second
            }

    memoryMap.values.sum().apply { println(this) }
} //13476250121721

fun part2() {
    var mask = 0L.toString(2)
    val memoryMap = mutableMapOf<Long, Long>()
    getInput().map { it.split(" = ") }
            .forEach { s ->
                if(s[0] == "mask") mask = s[1]
                else decodeMemoryAddress(mask, getMemoryInstructionPosition(s[0]))
                        .forEach { a -> memoryMap[a] = s[1].toLong() }
            }

    memoryMap.values.sum().apply { println(this) }
} //4463708436768

fun getMemoryInstructionPosition(instructionString: String): Long =
        "mem\\[([0-9]+)]".toRegex().find(instructionString)?.groupValues?.get(1)?.toLong()?:-1

fun decodeMemoryAddress(mask: String, address: Long): List<Long> {
    val addresses = mutableListOf(mask.replace("X", "0").toLong(2) or address)
    mask.mapIndexedNotNull { idx, char -> if(char == 'X') idx else null }
            .forEach { i ->
                addresses.addAll(addresses.map { address -> flipBitFromLeft(address, 36-i) })
            }
    return addresses.toList()
}

fun getBitFromLeft(address: Long, i: Int): Boolean =
        (address shr i-1) and 1L == 1L

fun flipBitFromLeft(address: Long, i: Int): Long {
    val diff = (2.0).pow(i-1).toLong()
    val existingBit = getBitFromLeft(address, i)
    return if(existingBit) address - diff else address + diff
}


