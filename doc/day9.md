# Day 9: Encoding Error
[view source](/src/main/kotlin/day9/Day9.kt)
## Part 1
### Problem
Given a list of integers, find a number for which any two the previous 25 values
add up to the that number.
### Solution
I wanted to slice up by input in substring of length 25 to be evaluated, paired with
our desired value. So I used my new favorite function of `fold`!
```
fun <T> List<T>.rollingSlicePairedToFinal(amount: Int): List<Pair<T, List<T>>> =
    foldIndexed(mutableListOf<Pair<T, List<T>>>()) { idx, acc, _ ->
        if(idx-amount+1 >= 0) acc += this[idx] to slice(idx-amount+1 until idx)
        acc
    }.toList()
```
Then I just had to follow through with a search. The `firstOrNull` is a little complex, but
is just subtracting each number in the sub-list from the 26th value, then checking if that difference exists
in the sub-list.
```
    File("src/main/resources/day9_input.txt").readLines()//.asSequence()
            .map { it.toLong() }
            .rollingSlicePairedToFinal(26)
            .firstOrNull { pair ->
                pair.second.map { i -> pair.first-i }
                        .none() { pair.second.contains(it) }
            }
            .apply { println(this?.first) }
```
## Part 2
### Problem
Find two or more consecutive numbers (in the same input) that add up to the answer from part 2.
### Solution
My idea is to start with a list of our inputs, then add the input from adjacent index and loop until
our desired value shows itself. To aid in tracking this running tally and it's components I
first created a data class with some convenience functions.
```
data class RunningTally(var total: Long, private val values: MutableList<Long>) {
    operator fun plusAssign(element: Long) {
        this.values += element
        total += element
    }

    fun first(): Long = values.first()
    fun size(): Int = values.size
    fun sumOfMinAndMax(): Long = (values.minOrNull()?:0) + (values.maxOrNull()?:0)
}
```
Then I created an extension function to iterate on a `List<RunningTally>` until the goal is reached.
```
fun List<RunningTally>.iterateRunningTally(goal: Long) =
    (indices).takeWhile { _ ->
        apply {
            forEachIndexed { idx, element ->
                if (idx + element.size() < size) element += this[idx + element.size()].first()
            }
        }.none { it.total == goal }
    }
```
So now we can initialize our base tallies, iterate them, and find the answer.
```
    val goal: Long = 776203571
    File("src/main/resources/day9_input.txt").readLines()
            .map { it.toLong() }
            .map { RunningTally(it, mutableListOf(it)) }
            .apply { iterateRunningTally(goal) }
            .firstOrNull { it.total == goal }
            .apply { println(this?.sumOfMinAndMax()) }
```