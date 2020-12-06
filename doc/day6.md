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

## Part 2
### Problem
Same as above, but now instead of unique characters in the input we want
to know which characters are the same between lines in the same group.
### Solution
The operation we want here is an `intersection`, which returns
the elements which are shared between two sets. For read-ability
I opted to create an extension function to handle the intersection of a
list of strings.
```
fun List<String>.intersectAll(): Set<Char> =
        this.map { it.toSet() }.reduce { acc, element -> acc intersect element }
```
Using this, the mapping and aggregate functions can be updated as such:
```
    File("src/main/resources/day6_input.txt").readLines()
            .foldToBlankLineSeparated("\n")
            .map { group -> group.split("\n").intersectAll() }
            .map { it.toSet().size }
            .sum()
            .apply { println(this) }
```