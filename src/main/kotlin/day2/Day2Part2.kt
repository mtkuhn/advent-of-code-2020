package day2

import java.io.File

fun main() {

    val lines = File("src/main/resources/day2_input.csv").readLines().asSequence()
    val count = lines.map { it.split("-", " ", ": ") }
        .map { split -> PolicyAndPassword(split[0].toInt(), split[1].toInt(), split[2][0], split[3]) }
        .count { pp -> (pp.password[pp.min-1] == pp.letter).xor(pp.password[pp.max-1] == pp.letter) }
    println(count)

}