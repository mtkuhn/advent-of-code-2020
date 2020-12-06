# Day 5: Custom Customs
[view source](/src/main/kotlin/day6/Day6.kt)
## Part 1
### Problem
Given a list of input strings separated by blank lines, determine the unique
characters in that grouping of the input. The answer is the sum of the size
of each group.
### Solution
Since I previously solved the issue of separating input by blank lines, I opted
to re-use my `fold` solution from part 4, this time doing so as an extension function.

This left the simple matter of converting each group into a set (which de-duplicates),
finding the size of each set, and summing the total:
```
    File("src/main/resources/day6_input.txt").readLines()
            .foldToBlankLineSeparated()
            .map { group -> group.filter { it.isLetter() } }
            .map { it.toSet().size }
            .sum()
            .apply { println(this) }
```