package day4

import java.io.File

fun main() {
    val requiredFields = listOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")
    File("src/main/resources/day4_input.txt")
            .readLines()
            .fold(mutableListOf("")) { list, line  ->
                if(line.isBlank()) list.add("")
                if(list.last().isBlank()) list[list.lastIndex] += line
                else list[list.lastIndex] += " $line"
                list
            }
            .map { passport ->
                passport.split(" ", ",")
                        .associate { element ->
                            val keyValue = element.split(":")
                            keyValue[0] to keyValue[1]
                        }
            }.count {
                it.keys.containsAll(requiredFields)
            }
            .apply { println(this) }
}