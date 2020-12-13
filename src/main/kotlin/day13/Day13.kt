package day13

import kotlin.math.max

val inputLines = """
    1000067
    17,x,x,x,x,x,x,x,x,x,x,37,x,x,x,x,x,439,x,29,x,x,x,x,x,x,x,x,x,x,13,x,x,x,x,x,x,x,x,x,23,x,x,x,x,x,x,x,787,x,x,x,x,x,x,x,x,x,41,x,x,x,x,x,x,x,x,19
""".trimIndent().lines()

fun main() {
    //part1()
    part2()
}

fun part1() {
    val goal = inputLines[0].toInt()
    inputLines[1].split(",")
            .mapNotNull { it.toIntOrNull() }
            .map { id -> id to id - goal%id }
            .minByOrNull { it.second }
            .let { (it?.first?:0) * (it?.second?:0) }
            .apply { println(this) }
}

fun part2() {
    //pairs are (offset to frequency)
    val buses = inputLines[1].split(",")
            .map { it.toLongOrNull() }
            .mapIndexedNotNull { idx, it ->
                if(it != null) BusSchedule(idx.toLong(), it)
                else null
            }

    var acc = buses[0]
    buses.drop(1).forEach() { bus ->
        acc = findAlignmentOf(bus, acc)
    }
    println(acc.offset)
}

data class BusSchedule(val offset: Long, val freq: Long)

fun findAlignmentOf(bus: BusSchedule, acc: BusSchedule): BusSchedule =
        generateSequence(acc.offset) { it + acc.freq }
                .first { i -> (bus.offset + i) % bus.freq == 0L }
                .let { BusSchedule(it, bus.freq*acc.freq) }