package day22

import java.io.File
import java.util.*

fun main() {
    val deckA = File("src/main/resources/day22_player1.txt").readLines()
            .map { it.toInt() }.let { Deck(it.toMutableList()) }
    val deckB = File("src/main/resources/day22_player2.txt").readLines()
            .map { it.toInt() }.let { Deck(it.toMutableList()) }
    Game(deckA, deckB)
            .apply { this.playGame() }
            .getScore()
            .apply { println(this) }
}

class Deck(private val cards: MutableList<Int>) {

    fun draw(): Int = cards.removeFirst()

    fun add(card1: Int, card2: Int) {
        cards.addAll(listOf(card1, card2))
    }

    fun size(): Int = cards.size

    fun firstN(size: Int): Deck = Deck(cards.slice(0 until size).toMutableList())

    fun getScore(): Long = cards.foldIndexed(0L) { idx, acc, card -> acc + (card*(cards.size-idx)) }

    override fun toString() = cards.toString()

}

class Game(private val deckA: Deck, private val deckB: Deck, private val played: MutableSet<String> = mutableSetOf()) {

    fun playGame(): Player {
        while(deckA.size() > 0 && deckB.size() > 0) {
            if(!played.add(uniqueForDecks())) return Player.PLAYER_A
            playRound()
        }
        return if(deckA.size() > 0) Player.PLAYER_A else Player.PLAYER_B
    }

    private fun playRound(): Player {
            val cardA = deckA.draw()
            val cardB = deckB.draw()
            val winner =
                    if(deckA.size() >= cardA && deckB.size() >= cardB) {
                        Game(deckA.firstN(cardA), deckB.firstN(cardB)).playGame()
                    } else {
                        if(cardA > cardB) Player.PLAYER_A
                        else Player.PLAYER_B
                    }

            if(winner == Player.PLAYER_A) deckA.add(cardA, cardB)
            else deckB.add(cardB, cardA)

            return winner
    }

    fun getScore(): Long =
            if(deckA.size() > deckB.size()) deckA.getScore()
            else deckB.getScore()

    fun uniqueForDecks() = "$deckA$deckB"

    enum class Player { PLAYER_A, PLAYER_B }

}