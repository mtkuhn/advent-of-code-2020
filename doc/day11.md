# Day 11: Seating System
[view source](/src/main/kotlin/day11/Day11.kt)
## Part 1
### Problem
Using a set of iterative rules that update seat occupancy, determine the count of occupied seats once the iterations
of the rules result in a steady state where no more changes are necessary. The input
is a grid of character where `L` is an empty seat, `#` is an occupied seat, and `.` is floor space.

The rules are:
>1. If a seat is empty (L) and there are no occupied seats adjacent to it, the seat becomes occupied.
>2. If a seat is occupied (#) and four or more seats adjacent to it are also occupied, the seat becomes empty.
>3. Otherwise, the seat's state does not change.

### Solution
First, I'm making use of `Pair<Int, Int>` for coordinates, so I need a couple of
convenience functions around this.
```
operator fun Pair<Int, Int>.plus(pos: Pair<Int, Int>) = first+pos.first to second+pos.second

operator fun Pair<Int, Int>.times(scale: Int) = first*scale to second*scale
```

I opted to encapsulate my data (the grid) into a class called `SeatingGrid`.
This wasn't really necessary, but I still like the structure it provides to the code 
(I originally had grander plans for this class).

To start, I want another convenience function for data access and a function to use
at the end for counting occupied seats. I have also enumerated the 8 cardinal directions
in a companion object.
```
class SeatingGrid(private var data: List<String>) {

    private operator fun get(pos: Pair<Int, Int>): Char = data[pos.first][pos.second]

    fun countOccupiedSeats(): Int = data.map { line -> line.count { it == '#' } }.sum()

    companion object {
        private val directions =
                sequenceOf((-1 to -1), (-1 to 0), (-1 to +1), (0 to -1), (0 to +1), (+1 to -1), (+1 to 0), (+1 to +1))
    }
```
Next we need the ability to pull the adjacent characters for further review.
```
    private fun isInBounds(pos: Pair<Int, Int>): Boolean =
            pos.first in (data.indices) && pos.second in (data[0].indices)

    private fun getAdjacentChars(pos: Pair<Int, Int>) =
            directions.map { it+pos }.filter { isInBounds(it) }.map { this[it] }
```
Using these, we can create a function to run an iteration of the rules. Here's what it does.
1. Iterate over all characters and map to the new strings. In all cases we record if we are making any changes 
(so we don't have to iterate over the grid again).
    2. When an empty seat is found, determine if it's nearby chars require a change to occupied.
    3. When an occupied seat is found, determine if it's nearby chars require a change to empty.
    4. Otherwise keep the same char.
2. Return if we changed anything.
```
    private fun runRulesAndReturnIsUpdated(
            occToEmptyThreshold: Int, getChars: (Pair<Int, Int>) -> Sequence<Char>): Boolean {
        var updated = false
        data = data.asSequence().mapIndexed { x, line ->
            line.asSequence().mapIndexed { y, char ->
                if (char == 'L' && !getChars(x to y).any { it == '#' }) {
                    updated = true
                    '#'
                }
                else if (char == '#' && getChars(x to y).count { it == '#' } >= occToEmptyThreshold) {
                    updated = true
                    'L'
                }
                else char
            }.joinToString(separator = "")
        }.toList()
        return updated
    }
```
Then it's a simple while loop until we get to steady state. You may have noticed I take
a lamba for the character-getting strategy, this is to help facilitate code re-use in part 2.
```
    private fun iterateRulesToSteadyState(occToEmptyThreshold: Int, getChars: (Pair<Int, Int>) -> Sequence<Char>) {
        var updated = true
        while(updated) { updated = runRulesAndReturnIsUpdated(occToEmptyThreshold, getChars) }
    }
```
## Part 2
### Problem
Same rules, just in instead of adjacent chars we want the first seat along lines in 8
directions.
### Solution
All we need to alter is how we find the characters to evaluate (the lamba we prepared for).

We iterate over the directions (this time being interpreted as slopes) until we either hit
a chair or go out of bounds (which is floor space), stopping at the first chair found.
```
    private fun getVisibleChars(pos: Pair<Int, Int>) =
            directions.map { slope -> getFirstCharVisibleInSlope(slope, pos) }

    private fun getFirstCharVisibleInSlope(slope: Pair<Int, Int>, start: Pair<Int, Int>): Char =
            (1..maxOf(data.size, data[0].length)).asSequence().map {
                val pos = start+(slope*it)
                if(isInBounds(pos)) this[pos] else '.'
            }.firstOrNull { it != '.' }?:'.'
```

