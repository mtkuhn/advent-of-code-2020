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
    this.foldIndexed(mutableListOf<Pair<T, List<T>>>()) { idx, acc, _ ->
        if(idx-amount+1 >= 0) acc += this[idx] to this.slice(idx-amount+1..idx-1)
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