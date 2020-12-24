package day22

import java.io.File

fun main() {
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