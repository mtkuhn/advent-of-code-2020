package day1

import java.io.File
import java.util.stream.Collectors

fun main() {

    val total = 2020
    val numberOfValuesToFind = 3

    var candidates = listOf(ExpenseValueCandidateTracker(emptyList(), getInput()))
    repeat(numberOfValuesToFind) {
        candidates = candidates.flatMap { it.expandExpenseValueCandidatesSelections(total) }
    }
    val matchingCandidate = candidates.firstOrNull { it.selectedCandidates.sum() == total }

    if(matchingCandidate == null) {
        println("No solution.")
    } else {
        val selectedValues = matchingCandidate.selectedCandidates
        val solution = matchingCandidate.calcProductOfSelectedValues()
        println("$selectedValues add up to $total. Solution is $solution.")
    }

}

/**
 * Grab the hard-coded input file.
 */
fun getInput(): List<Int> {
    return File("src/main/resources/day1_input.csv").readLines().stream()
        .map{ it.toInt() }
        .collect(Collectors.toList())
}

/**
 * This is essentially a node is a search tree of values. It tracks the possible selections we've made vs the remaining
 * values to chose from.
 */
data class ExpenseValueCandidateTracker(val selectedCandidates: List<Int>, val remainingCandidates: List<Int>) {


    /**
     * Extend the list of selected values with all that still satisfy the condition that the sum of selected values
     * is less than the 'total'(2020), and updated the remaining values as appropriate.
     */
    fun expandExpenseValueCandidatesSelections(total: Int): List<ExpenseValueCandidateTracker> {
        return this.remainingCandidates
            .filter { selectedCandidates.sum() + it <= total }
            .run {
                this.map {
                    ExpenseValueCandidateTracker(selectedCandidates+it, this.minus(it))
                }
            }
    }

    fun calcProductOfSelectedValues(): Int {
        var x = 1
        selectedCandidates.iterator().apply {
            while(hasNext()) {
                x *= next()
            }
        }
        return x
    }
}