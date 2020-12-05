package day5

import java.io.File
import kotlin.math.pow
import kotlin.math.roundToInt

fun main() {
    File("src/main/resources/day5_input.txt").readLines().asSequence()
            .map {
                it.slice(0..6) to it.slice(7..9)
            }
            .map {
                it.first.findBinaryPartitionValue(128, 'B') to
                it.second.findBinaryPartitionValue(8, 'R')
            }
            .map {
                (it.first*8)+it.second
            }
            .maxOrNull()
            .apply { println(this) }
}

fun String.findBinaryPartitionValue(maxValue: Int, highChar: Char): Int =
    this.foldIndexed(0) { idx, seat, char ->
        seat + if(char == highChar) maxValue/2.pow(idx+1) else 0
    }

fun Int.pow(exp: Int): Int = this.toFloat().pow(exp).roundToInt()