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