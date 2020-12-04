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
                        && it["byr"].isYearInRange(1920..2002)
                        && it["iyr"].isYearInRange(2010..2020)
                        && it["eyr"].isYearInRange(2020..2030)
                        && it["hgt"].isValidHeight()
                        && it["hcl"]?.matches("^#[0-9a-f]{6}$".toRegex())?:false
                        && it["ecl"].isValidEyeColor()
                        && it["pid"]?.matches("^\\d{9}$".toRegex())?:false
            }
            .apply { println(this) }
}

fun String?.isYearInRange(range: IntRange): Boolean =
        this != null && matches("\\d{4}".toRegex()) && toInt() in range

fun String?.isValidHeight(): Boolean =
        this != null
                && (isValidUnitAndRange("cm", 150..193) || isValidUnitAndRange("in", 59..76))

fun String.isValidUnitAndRange(unit: String, range: IntRange): Boolean =
        matches("^\\d+${unit}$".toRegex())
                && substring(0 until length-unit.length).toInt() in range

fun String?.isValidEyeColor(): Boolean =
        this != null && listOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth").contains(this)
