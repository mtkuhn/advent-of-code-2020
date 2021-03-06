# Day 7: Handy Haversacks
[view source](/src/main/kotlin/day7/Day7.kt)
## Part 1
### Problem
Given nested rules about colored bags holding other colored bags, determine
the number of different colors of bag that can eventually hold a `shiny gold bag`.
A couple of example rules:
```
vibrant plum bags contain 5 faded blue bags, 6 dotted black bags.
faded blue bags contain no other bags.
```
### Solution
The number of bags of the same color held by another bag doesn't appear
to matter for the question, but I have a sneaking suspicion that it
will in part two, so I want to capture this info.
We need to start by getting our input. I have another suspicion that
my data structure is going to get slightly confusing, so I'll first define
a data class with clear variable names and a method it can use to convert input.
```
data class BagRule(val color: String, val contents: List<Pair<Int, String>>) {
    companion object {
        fun fromString(input: String): BagRule {
            val split = input.split(" bags contain ")
            val subjectColor = split[0]
            val containedBags = split[1].split(", ".toRegex()).toList()
                    .flatMap {
                        "(\\d+) (.+) bags?".toRegex().findAll(it).map { b ->
                            b.groupValues[1].toInt() to b.groupValues[2]
                        }
                    }
            return BagRule(subjectColor, containedBags)
        }
    }
}
```
Now we move on to the actual data crunching. I'll start with our `shiny gold bag`, 
find all direct containers, and record those as a set. Then recursively add direct 
containers of those results.
```
fun List<BagRule>.findContainingBagColors(evalColor: String): Set<String> {
    val directColors = this.filter { r -> r.contents.map { p -> p.second }.contains(evalColor) }
            .map { it.color }
            .toSet()
    val indirectColors = directColors.flatMap { this.findContainingBagColors(it) }.toSet()
    return directColors + indirectColors
}
```
This makes it easy to invoke for our initial color and run a count.
```
    File("src/main/resources/day7_input.txt").readLines()
            .map { BagRule.fromString(it) }
            .findContainingBagColors("shiny gold")
            .count()
            .apply { println(this) }
```

## Part 2
### Problem
Using the same rules, find the number of bags required to be carried within a `shiny gold bag`.
### Solution
This time we need to traverse the rules in the opposite direction. Luckily I already included
the number of bags! Again I'm going with a recursive approach. We find the rule for the given
color, then multiply the number of bags for each contained color.
```
fun List<BagRule>.countBagsContainedBy(evalColor: String): Int =
        this.find { r -> r.color == evalColor }
            ?.contents
                ?.map { it.first*(this.countBagsContainedBy(it.second)+1) }
                ?.sum()?:0
```
Which makes the main method simple:
```
    File("src/main/resources/day7_input.txt").readLines()
            .map { BagRule.fromString(it) }
            .countBagsContainedBy("shiny gold")
            .apply { println(this) }
```