# Day 16: Ticket Translation
[view source](/src/main/kotlin/day16/Day16.kt)
## Part 1
### Problem
We are given ticket field definitions as a name and ranges of possible values.
We are also given a table of tickets with unlabeled values. The field positions are
consistent, but it's unknown how to assign a field type to an index.
Find all ticket values that are invalid for any field and get the sum of those values.
### Solution
First the boring stuff. I created a data class for the Field Definition, which holds
a name and a list of allowed `IntRange` values. I also created a function to load this
from the input, as well as loading all ticket data as a list of `Int`.
```
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

fun String.toRangeList(): List<IntRange> =
        split(" or ").map { range ->
            val parts = range.split("-")
            parts[0].toInt() .. parts[1].toInt()
        }
```
I also defined this function to tell if a given number meets the criteria
of all field definitions.
```
fun Int.isInvalidForAllFieldDefinitions(criteria: List<FieldDefinition>) =
        !criteria.any { it.canAccept(this) }
```
This is simple now, we just filter each ticket to values that are invalid, flatten the lists,
then take the sum.
```
fun part1() {
    val fieldDefinitions = getFieldDefinitionsFromInput()
    val otherTicketValues = getTicketsFromInput()

    val invalidFields = otherTicketValues.flatMap { ticket ->
        ticket.filter { field -> field.isInvalidForAllFieldDefinitions(fieldDefinitions) }
    }

    println(invalidFields.sum())
}
```
## Part 1
### Problem
Discarding tickets with invalid values from part1, now attempt to determine how the
indexes map to field names.
The answer is the product of all fields in our ticket that start with 'departure'.
### Solution
After discarding part 1 tickets, we need to first enumerate all possibilities per index.
I also initialize an empty list of known indexes.
The possible fields per index are those for which all tickets meet the ranges.
```
    val knownFieldIndexes = mutableListOf<Pair<Int, FieldDefinition>>()
    val possibleFieldsByIndex: MutableList<Pair<Int, MutableList<FieldDefinition>>> =
            (fieldDefinitions.indices).map { idx ->
                idx to fieldDefinitions.filter { def ->
                    validTickets.all { t -> def.canAccept(t[idx]) }
                }.toMutableList()
            }.apply { println(this.map { it.second.size }) }.toMutableList()
```
Now we can loop through the possibilities looking for anything that has only once possibility.
Any such indexes are then moved to known list, and removed from the list of possibilities on
all indexes. As we reduce the possibilities on each index, more will become known on the
next loop.
```
    while(possibleFieldsByIndex.any { it.second.size > 0 }) {
        val foundFields: List<Pair<Int, FieldDefinition>> =
                possibleFieldsByIndex.filter { it.second.size == 1 }
                        .map { it.first to it.second[0] }
        possibleFieldsByIndex.forEach {
            it.second -= foundFields.map { f -> f.second }
        }
        knownFieldIndexes += foundFields
    }
```
I'm very glad this worked out, as I had a fear some higher-level sudoku logic
might be necessary if we hit a point where all indexes had multiple possibilities.

Finally, we find our departure fields and associate them to values in our ticket:
```
    knownFieldIndexes.filter { it.second.name.startsWith("departure") }
            .map { myTicket[it.first].toLong() }
            .reduce { acc, element -> acc * element }
            .apply { println(this) }
```
