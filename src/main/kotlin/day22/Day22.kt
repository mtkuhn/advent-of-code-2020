package day22

import java.io.File


fun main() {
    part1()
    part2()
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
    }.apply { println(this) }
}

fun part2() {
    val player1 = File("src/main/resources/day22_player1.txt").readLines().map { it.toLong() }.toMutableList()
    val player2 = File("src/main/resources/day22_player2.txt").readLines().map { it.toLong() }.toMutableList()

    //val player1 = mutableListOf<Long>(9, 2, 6, 3, 1)
    //val player2 = mutableListOf<Long>(5, 8, 4, 7, 10)

    val winningDeck = play(player1, player2, 0).second
    println(winningDeck)

    winningDeck.foldIndexed(0L) { idx, acc, card ->
        acc + (card*(winningDeck.size-idx))
    }.apply { println(this) }

    //16455 too low
}

fun play(player1: MutableList<Long>, player2: MutableList<Long>, level: Int): Pair<Int, List<Long>> {

    val playedDecks = mutableSetOf<Pair<List<Long>, List<Long>>>()

    while(player1.isNotEmpty() && player2.isNotEmpty()) {

        println("--Level: $level --------------") //todo
        println("player1 deck: $player1") //todo
        println("player2 deck: $player2") //todo

        val decks = player1.toList() to player2.toList()
        if(playedDecks.contains(decks)) {
            println("player 1 wins the game") //todo
            return 1 to player1
        } else playedDecks += decks

        val card1 = player1.removeFirst()
        val card2 = player2.removeFirst()
        if(player1.size >= card1 && player2.size >= card2) {
            val winner = play(player1.toMutableList(), player2.toMutableList(), level+1)
            if(winner.first == 1) {
                println("player 1 wins the round") //todo
                player1.apply{ add(card1); add(card2) }
            } else {
                println("player 2 wins the round") //todo
                player2.apply{ add(card2); add(card1) }
            }
        } else {
            if(card1 > card2) {
                println("player 1 wins the round") //todo
                player1.apply{ add(card1); add(card2) }
            } else {
                println("player 2 wins the round") //todo
                player2.apply{ add(card2); add(card1) }
            }
        }
    }

    val winner = if(player1.isNotEmpty()) 1 to player1 else 2 to player2
    println("player ${winner.second} wins the game") //todo
    return winner
}