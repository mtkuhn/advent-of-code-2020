package day22

import java.io.File
import java.time.LocalDateTime
import java.util.*


fun main() {
    println(LocalDateTime.now())
    test1()
    test2()
    part1()
    println(LocalDateTime.now())
    part2()
    println(LocalDateTime.now())
}

fun part1() {
    val player1 = File("src/main/resources/day22_player1.txt").readLines().map { it.toLong() }.toMutableList()
    val player2 = File("src/main/resources/day22_player2.txt").readLines().map { it.toLong() }.toMutableList()

    while(player1.isNotEmpty() && player2.isNotEmpty()) {
        val card1 = player1.removeFirst()
        val card2 = player2.removeFirst()
        if(card1 > card2) player1.apply{ add(card1); add(card2) }
        else player2.apply{ add(card2); add(card1) }
    }

    val winner = if(player1.isNotEmpty()) player1 else player2
    winner.foldIndexed(0L) { idx, acc, card ->
        acc + (card*(winner.size-idx))
    }.apply { println("part1: $this") }
}

fun part2() {
    val player1 = File("src/main/resources/day22_player1.txt").readLines().map { it.toInt() }
    val player2 = File("src/main/resources/day22_player2.txt").readLines().map { it.toInt() }

    val winningDeck = play(player1, player2, 0).second
    println(winningDeck)

    winningDeck.foldIndexed(0L) { idx, acc, card ->
        acc + (card*(winningDeck.size-idx))
    }.apply { println("part2: $this") }

    //16455 too low
}

fun test1() {
    val player1 = mutableListOf(9, 2, 6, 3, 1)
    val player2 = mutableListOf(5, 8, 4, 7, 10)

    val winningDeck = play(player1, player2, 0).second

    winningDeck.foldIndexed(0L) { idx, acc, card ->
        acc + (card*(winningDeck.size-idx))
    }.apply { println("test1: $this") }
}

fun test2() {
    val player1 = mutableListOf(43, 19)
    val player2 = mutableListOf(2, 29, 14)

    val winningDeck = play(player1, player2, 0).second
    println(winningDeck)

    winningDeck.foldIndexed(0L) { idx, acc, card ->
        acc + (card*(winningDeck.size-idx))
    }.apply { println("test2: $this") }
}

fun play(player1In: List<Int>, player2In: List<Int>, level: Int): Pair<Int, List<Int>> {

    //println("-!!---new game---!!-".prependIndent("  ".repeat(level)))

    val playedDecks = mutableSetOf<Int>()

    val player1 = player1In.toMutableList()
    val player2 = player2In.toMutableList()

    while(player1.isNotEmpty() && player2.isNotEmpty()) {

        if(level == 0) println("new loop")

        //println("----new round----".prependIndent("  ".repeat(level)))
        //println("p1:$player1".prependIndent("  ".repeat(level)))
        //println("p2:$player2".prependIndent("  ".repeat(level)))

        val deckString = Objects.hash(player1, player2) //"$player1|$player2"
        if(deckString in playedDecks) {
            //println("player 1 wins for recurring decks".prependIndent("  ".repeat(level)))
            return 1 to player1
        } else {
            playedDecks += deckString
        }

        val card1 = player1.removeFirst()
        val card2 = player2.removeFirst()
        //println("$card1, $card2 drawn".prependIndent("  ".repeat(level)))
        //println("player1.size=${player1.size}, player2.size=${player2.size}".prependIndent("  ".repeat(level)))
        val roundWinner =
                if(player1.size >= card1 && player2.size >= card2) {
                    //println("player1.size=${player1.size}, player2.size=${player2.size}".prependIndent("  ".repeat(level)))
                    play(player1, player2, level+1).first
                } else {
                    if(card1 > card2) 1 else 2
                }

        if(roundWinner == 1) {
            //println("player 1 wins".prependIndent("  ".repeat(level)))
            player1.apply{ add(card1); add(card2) }
        }
        else {
            //println("player 2 wins".prependIndent("  ".repeat(level)))
            player2.apply{ add(card2); add(card1) }
        }
    }

    return if(player1.isNotEmpty()) 1 to player1 else 2 to player2
}