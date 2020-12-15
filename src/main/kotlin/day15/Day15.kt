package day15

fun main() {
    solve(2020) //part 1
    solve(30000000) //part 2
}

fun solve(endTurn: Int) {
    val input = listOf(1,2,16,19,18,0)
    val numberToIndexMap = input.mapIndexed { idx, n -> n to idx+1 }.toMap().toMutableMap()
    val initialTurn = Turn(input.size, input.last())
    generateSequence(initialTurn) { findNumberToSpeakAndAddLastTurnToMap(numberToIndexMap, it) }
            .first { it.turnNumber == endTurn }
            .apply { println(this.numberSpoken) }
}

data class Turn(val turnNumber: Int, val numberSpoken: Int)

fun findNumberToSpeakAndAddLastTurnToMap(numberToIndexMap: MutableMap<Int, Int>, lastTurn: Turn): Turn {
    val turn = findNumberToSpeak(numberToIndexMap, lastTurn)
    numberToIndexMap[lastTurn.numberSpoken] = lastTurn.turnNumber
    return turn
}

fun findNumberToSpeak(numberToIndexMap: Map<Int, Int>, lastTurn: Turn): Turn {
    val lastNumberSpokenAt = numberToIndexMap[lastTurn.numberSpoken]
    return if(lastNumberSpokenAt == null) Turn(lastTurn.turnNumber + 1, 0)
    else Turn(lastTurn.turnNumber + 1, lastTurn.turnNumber - lastNumberSpokenAt)
}