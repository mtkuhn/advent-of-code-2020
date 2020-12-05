package day5

import java.io.File
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.time.seconds

fun main() {
    partTwo()
}

fun partOne() {
    File("src/main/resources/day5_input.txt").readLines().asSequence()
            .map {
                it.slice(0..6) to it.slice(7..9)
            }
            .map {
                it.first.findBinaryPartitionValue(128, 'B') to
                        it.second.findBinaryPartitionValue(8, 'R')
            }
            .map { it.first*8+it.second }
            .maxOrNull()
            .apply { println(this) }
}

fun partTwo() {
    val knownSeats = File("src/main/resources/day5_input.txt").readLines().asSequence()
            .map {
                it.slice(0..6) to it.slice(7..9)
            }
            .map {
                Seat.fromPosition(it.first.findBinaryPartitionValue(128, 'B'),
                        it.second.findBinaryPartitionValue(8, 'R'))
            }
            .toList()

    val knownSeatIds = knownSeats.map{ it.id }

    val missingSeats = knownSeats.groupBy { it.row }
            .filter { it.value.size < 8 }
            .map { rowMapEntry -> rowMapEntry.key to (0..7)-rowMapEntry.value.map { it.col }.toList() }
            .flatMap { mapEntry ->
                mapEntry.second.map {
                    Seat.fromPosition(mapEntry.first, it)
                }
            }.filter {
                knownSeatIds.contains(it.id+1) && knownSeatIds.contains(it.id-1)
            }
    println(missingSeats)
}

data class Seat(val id: Int, val col: Int, val row: Int) {
    companion object {
        fun fromPosition(col: Int, row: Int): Seat = Seat(col*8+row, row, col)
    }
}

fun String.findBinaryPartitionValue(maxValue: Int, highChar: Char): Int =
    this.foldIndexed(0) { idx, seat, char ->
        seat + if(char == highChar) maxValue/2.pow(idx+1) else 0
    }

fun Int.pow(exp: Int): Int = this.toFloat().pow(exp).roundToInt()