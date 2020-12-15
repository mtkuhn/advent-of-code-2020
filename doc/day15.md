# Day 15: Rambunctious Recitation
[view source](/src/main/kotlin/day15/Day15.kt)
## Part 1
### Problem
The elves are playing a game where they take turns saying numbers. The rules are:
1. Each player take a turn reading off a starting number from a list.
2. The following turns require them to speak a number based on the most recently spoken
number. 
    1. If that was the first time the number has been spoken, the current player says 0.
    2. Otherwise, the number had been spoken before; the current player says 
how many turns apart the number is from when it was previously spoken.

What number is spoken on turn 2020?

###Solution
To start, I created a data class to improve readability.
```
data class Turn(val turnNumber: Int, val numberSpoken: Int)
```
Always optimizing on these puzzles, I wanted a Map that can quickly take a spoken
number and return it's last turn number (or index). I prime it with the initial inputs.
```
    val input = listOf(1,2,16,19,18,0)
    val numberToIndexMap = input.mapIndexed { idx, n -> n to idx+1 }.toMap().toMutableMap()
```
Using this, I can create a function to take the above map and the previous turn in order
to produce the results of this turn.
```
fun findNumberToSpeak(numberToIndexMap: Map<Int, Int>, lastTurn: Turn): Turn {
    val lastNumberSpokenAt = numberToIndexMap[lastTurn.numberSpoken]
    return if(lastNumberSpokenAt == null) Turn(lastTurn.turnNumber + 1, 0)
    else Turn(lastTurn.turnNumber + 1, lastTurn.turnNumber - lastNumberSpokenAt)
}
```
I plan to loop this function, but when I call it I also need to make sure 
the adding of numbers to the `Map` lags slightly. If we add the previous turn's number
too quickly we might overwrite important data needed for this step, so we only 
push a new `Map` entry for the *last* turn once we are done with *this* turn.
```
fun findNumberToSpeakAndAddLastTurnToMap(numberToIndexMap: MutableMap<Int, Int>, lastTurn: Turn): Turn {
    val turn = findNumberToSpeak(numberToIndexMap, lastTurn)
    numberToIndexMap[lastTurn.numberSpoken] = lastTurn.turnNumber
    return turn
}
```
Now that we have that sorted out, we can use a sequence generator to iterate
through, starting with the last starting number. It looks for the first element
to reach our `endTurn` turn number and prints the result.
```
    val initialTurn = Turn(input.size, input.last())
    generateSequence(initialTurn) { findNumberToSpeakAndAddLastTurnToMap(numberToIndexMap, it) }
            .first { it.turnNumber == endTurn }
            .apply { println(this.numberSpoken) }
```
## Part 2
### Problem
Same problem, higher turn number: 30,000,000
### Solution
I had already optimized Part 1 with a `Map` to cache our spoken numbers, 
so there wasn't anything I had to do here but update the turn number we were
iterating toward. It took ~8 seconds (I don't have timing logs), but was very
reasonable.
