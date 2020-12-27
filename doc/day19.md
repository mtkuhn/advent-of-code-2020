# Day 19: Monster Messages
[view source](/src/main/kotlin/day19/Day19.kt)
## Part 1
### Problem
Given a list of rules and messages, determine how
many messages match rule `0`.
Rules might be a constant letter, a series of references
to other rules, and might include an or `|`.
For example:
```
0: 1 2
1: "a"
2: 1 3 | 3 1
3: "b"
```
Messages are a series of letters such as:
```
ababbb
bababa
```
### Solution
My solution on this was a little cheap, as I deferred most of the work to regex.

First, a couple of methods for parsing the input into rules.
1. Separate the rule id from the rule data. `2: 1 3 | 3 1` becomes `2` and `1 3 | 3 1`.
2. Separate the different sides of any 'or'. So now we have `1 3` and `3 1`.
3. Get rid of stray spaces and quotes and encapsulate everything in parentheses. `(((1)(3))|((3)(1)))`
```
fun getRuleMap(): Map<Int, String> =
        File("src/main/resources/day19_rules.txt").readLines()
                .map { it.parseRule() }
                .toMap()

fun String.parseRule(): Pair<Int, String> {
    val split = this.split(": ")
    var id = split[0].toInt()
    var options = split[1].split(" | ").joinToString(separator = "|") { s ->
        s.split(" ").joinToString(separator = "", prefix = "(", postfix = ")") { n ->
            "(${n.replace("\"", "")})"
        }
    }
    options = "\\(\\([a-zA-Z]+\\)\\)".toRegex().replace(options) { it.value.drop(2).dropLast(2) }
    return id to "($options)"
}
```
Next we iterate over each rules, replacing its references in all other rules. This gets us down
to just really long regexes containing only letters separated into groups.
```
    while(rules.values.any { it.contains("\\d".toRegex()) }) {
        rules.forEach() { ruleMapEntry ->
            "\\(\\d+\\)".toRegex().findAll(ruleMapEntry.value)
                    .map { it.value }
                    .forEach { foundNumber ->
                        val intNumber = foundNumber.replace("[^\\d]".toRegex(), "").toInt()
                        val replacement = rules[intNumber]!!.drop(1).dropLast(1)
                        rules[ruleMapEntry.key] = ruleMapEntry.value.replace(foundNumber, "($replacement)")
                    }
        }
    }
```
Finally, we compare the regexes to the input and get a count.
```
fun countMatchingMessages(ruleMap: Map<Int, String>): Int =
        File("src/main/resources/day19_input.txt").readLines()
                .filter { line ->
                    ruleMap.any { rule -> rule.value.toRegex().matches(line) }
                }
                .count()
```
## Part 1
### Problem
Two rules are replaced such that they now contain circular references.
```
8: 42 | 42 8
11: 42 31 | 42 11 31
```
### Solution
The problem itself indicated that a general solution probably isn't worth and to solve just
for these cases. So that's what I did. I hand-wrote the new regex for each and updated the
code to substitute them.

For rule `8` this was simple, it's just 42 repeating any number of times.

For rule `11`, the pattern needs to be balanced with repeats of `42` and `31` being evenly
weighted on each side. I learned that this is something that regex can handle, but not in the
standard implementation used by Java/Kotlin. Rather than bring in a new regex library, I just
brute-forced it by adding multiple regex groups to handle repeats up to 100 (which was plenty).
```
    val rules = getRuleMap().toMutableMap()
    rules[8] = "((42)+)" //353

    var rule11 = (1..100).map {
        "((42){$it}(31){$it})"
    }.joinToString(separator = "|", prefix="(", postfix= ")")
    rules[11] = rule11
```
It took a few seconds to run, but produced results! There's probably a better way to do this.