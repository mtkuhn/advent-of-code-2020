# Day 10: Adaptor Array
[view source](/src/main/kotlin/day10/Day10.kt)
## Part 1
### Problem
Given a list of jolt integers, add in a known value of 0 and another value that is 3 higher than
the highest number in the input. Linking each together within 3 jolts of each other, calculate the
differences in jolts. What is the number of 1-jolt differences multiplied by the number of 3-jolt differences?
### Solution
I realized that the order of jolts was simply a sort by Integer value, so this went very easily.
The `let` is adding in our two values that were not in the input. `zipWithNext` is pair each with the next
value in the sequence and immediately mapping it to the difference.
```
    File("src/main/resources/day10_input.txt").readLines().asSequence()
            .map { it.toLong() }
            .let { it.plus(sequenceOf(0, (it.maxOrNull()?:0)+3)) }
            .sorted()
            .zipWithNext { a, b -> b-a }
            .groupBy { it }
            .apply { println((this[1]?.size?:0) * (this[3]?.size?:0)) }
```
## Part 2
### Problem
Find the number of possible paths (not necessarily using all input values) that
we can use to connect from 0 to the highest value plus 3.
### Solution
My first attempt at this had me constructing a tree of possible values and counting
the paths found. This turned out to have terrible runtime.

The trick here is to realize that numbers that jump by 3 are a bottleneck in the
input. You can subdivide the problem by these gaps and take the product. 

You can go further than this too, all gaps in the input are either of 1 or 3. 
No numbers jump by 2. For this reason it was useful for me to define a function to 
determine the numbers of paths given the length of a sequence of consecutive numbers.
```
fun pathsInConsecutiveSequenceOfLength(length: Long): Long =
        if(length <= 2) 1
        else (1..minOf(3, length-1)).map { pathsInConsecutiveSequenceOfLength(length-it) }.sum()
```
Using this, we can convert the data in a list of lengths of consecutive numbers, all of which
are implied to be separated by 3. Then we determine the count of paths for each section and 
take the product.
```
    File("src/main/resources/day10_input.txt").readLines().asSequence()
            .map { it.toLong() }
            .let { it.plus(sequenceOf(0, (it.maxOrNull()?:0)+3)) }
            .sorted()
            .zipWithNext { a, b -> b-a } //differences between sorted numbers
            .fold(mutableListOf(1L)) { acc, n -> //convert the list of diffs into a count of consecutive numbers
                if(n == 3L) acc += 1L
                else acc[acc.lastIndex] += 1L
                acc
            }
            .map { pathsInConsecutiveSequenceOfLength(it) }
            .reduce { acc, n -> acc * n }
            .apply { println(this) }
```