package day1

import java.io.File

fun main() {

    val total = 2020
    val entries = File("src/main/resources/day1_input.csv").readLines()
            .map{ it.toInt() }

    val firstResult: Int? = entries.firstOrNull {
        entries.minusElement(it).contains(total - it)
    }

    if(firstResult == null) {
        println("No solution.")
    } else {
        val secondResult = 2020-firstResult
        val solution = firstResult*secondResult
        println("$firstResult and $secondResult add up to $total. Solution is $solution.")
    }

}

