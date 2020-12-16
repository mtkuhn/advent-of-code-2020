package day16

import java.io.File

fun main() {
    part1() //27870
    part2() //3173135507987
}

fun part1() {
    val fieldDefinitions = getFieldDefinitionsFromInput()
    val otherTicketValues = getTicketsFromInput()

    val invalidFields = otherTicketValues.flatMap { ticket ->
        ticket.filter { field -> field.isInvalidForAllFieldDefinitions(fieldDefinitions) }
    }

    println(invalidFields.sum())
}

fun part2() {
    val myTicket = listOf(107,109,163,127,167,157,139,67,131,59,151,53,73,83,61,89,71,149,79,137)
    val fieldDefinitions = getFieldDefinitionsFromInput()
    val validTickets = getTicketsFromInput().filter { ticket ->
        ticket.all { field -> !field.isInvalidForAllFieldDefinitions(fieldDefinitions) }
    }

    val knownFieldIndexes = mutableListOf<Pair<Int, FieldDefinition>>()
    val possibleFieldsByIndex: MutableList<Pair<Int, MutableList<FieldDefinition>>> =
            (fieldDefinitions.indices).map { idx ->
                idx to fieldDefinitions.filter { def ->
                    validTickets.all { t -> def.canAccept(t[idx]) }
                }.toMutableList()
            }.apply { println(this.map { it.second.size }) }.toMutableList()

    while(possibleFieldsByIndex.any { it.second.size > 0 }) {
        val foundFields: List<Pair<Int, FieldDefinition>> =
                possibleFieldsByIndex.filter { it.second.size == 1 }
                        .map { it.first to it.second[0] }
        possibleFieldsByIndex.forEach {
            it.second -= foundFields.map { f -> f.second }
        }
        knownFieldIndexes += foundFields
    }

    knownFieldIndexes.filter { it.second.name.startsWith("departure") }
            .map { myTicket[it.first].toLong() }
            .reduce { acc, element -> acc * element }
            .apply { println(this) }
}

data class FieldDefinition(val name: String, val allowedRanges: List<IntRange>) {
    fun canAccept(n: Int) = allowedRanges.any { n in it }
}

fun getTicketsFromInput(): List<List<Int>> =
    File("src/main/resources/day16_other_tickets.txt").readLines()
            .map { ticket ->
                ticket.split(",").map { value ->
                    value.toInt()
                }
            }

fun getFieldDefinitionsFromInput(): List<FieldDefinition> =
        File("src/main/resources/day16_field_def.txt").readLines()
                .map { FieldDefinition(it.substringBefore(": "), it.substringAfter(": ").toRangeList()) }

fun Int.isInvalidForAllFieldDefinitions(criteria: List<FieldDefinition>) =
        !criteria.any { it.canAccept(this) }

fun String.toRangeList(): List<IntRange> =
        split(" or ").map { range ->
            val parts = range.split("-")
            parts[0].toInt() .. parts[1].toInt()
        }