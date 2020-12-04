# Day 1: Report Repair

## Part 1

### Problem
Find two numbers from the list of input data that add up to 2020. Return the product of these numbers.

### Solution
I simply use a stream to iterate over the values, comparing each to the remaining values until I find one which satisfies the condition.

    val firstResult: Int? = entries.firstOrNull {
            entries.minusElement(it).contains(total - it)
    }

## Part 2

### Problem
Find three numbers that add up to 2020 and return the product.

### Solution
Though I could have simplified this to always look for exactly 3 numbers, 
I opted to generalize it to run for any number of values desired. 
This solution replaces my part 1 code completely, as the same code works in both cases. In theory,
I could run this for 4 values that sum to 2020, though no solution exists in that case.

It works by iteratively doing a breadth-wise expansion of options, 
filtering out impossibilities as it goes. There are likely faster ways to traverse the
tree, but this works well enough for the given dataset.
 
    fun expandExpenseValueCandidatesSelections(total: Int): List<ExpenseValueCandidateTracker> {
        return this.remainingCandidates
            .filter { it >= selectedCandidates.maxOrNull()?:0 }
            .filter { selectedCandidates.sum() + it <= total }
            .run {
                this.map {
                    ExpenseValueCandidateTracker(selectedCandidates+it, this.minus(it))
                }
            }
    }