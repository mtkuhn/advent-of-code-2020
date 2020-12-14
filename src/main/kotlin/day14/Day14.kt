package day14

import java.io.File

fun main() {
    part1()
    part2()
}

fun getInput(): List<String> =
        File("src/main/resources/day14_input.txt").readLines()

fun part1() {
    var mask = 0L to 0L
    var memoryMap = mutableMapOf<Long, Long>()
    getInput().map { it.split(" = ") }
            .forEach { s ->
                if(s[0] == "mask") {
                    mask = (s[1].replace("X", "0").toLong(2)
                            to s[1].replace("X", "1").toLong(2))
                }
                else {
                    val addr = "mem\\[([0-9]+)\\]".toRegex().find(s[0])?.groupValues?.get(1)?.toLong()?:-1
                    memoryMap[addr] = s[1].toLong() or mask.first and mask.second
                }
            }

    memoryMap.values.sum().apply { println(this) }
} //13476250121721

fun part2() {
    var mask = 0L.toString(2)
    var memoryMap = mutableMapOf<Long, Long>()
    getInput().map { it.split(" = ") }
            .forEach { s ->
                if(s[0] == "mask") {
                    mask = s[1]
                }
                else {
                    val addr = "mem\\[([0-9]+)\\]".toRegex().find(s[0])
                            ?.groupValues?.get(1)?.toLong()?:-1
                    decodeMemoryAddress(mask, addr).forEach() { a ->
                        memoryMap[a] = s[1].toLong()
                    }

                }
            }

    memoryMap.values.sum().apply { println(this) }
}

fun decodeMemoryAddress(mask: String, addr: Long): List<Long> {
    val addresses = mutableListOf(mask.replace("X", "0").toLong(2) or addr)
    mask.mapIndexedNotNull { idx, char -> if(char == 'X') idx else null }
            .forEach { i ->
                addresses.addAll(addresses.map { address -> flipBitAt(address, i) })
            }
    return addresses.toList()
}

fun flipBitAt(address: Long, i: Int): Long {
    var addressBits = address.toString(2)
    repeat(36-addressBits.length) { addressBits = "0$addressBits" } //pad with 0s
    val charReplacement = if(addressBits[i] == '1') '0' else '1' //flip the bit
    val newStr = addressBits.slice(0 until i)+charReplacement+
            addressBits.slice(i+1 until addressBits.length) //piece together
    return newStr.toLong(2)  //as long
}

