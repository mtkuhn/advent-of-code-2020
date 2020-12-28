package day25

fun main() {
    val key = Key(7L)
    val keyA = generateSequence(key) { it.loop() }.first { it.value == 3418282L }
    val keyB = generateSequence(key) { it.loop() }.first { it.value == 8719412L }
    generateSequence(Key(keyB.value)) { it.loop() }.elementAt(keyA.loops).apply { println(this) }
}

data class Key(val subjectNumber: Long, val loops: Int = 0, val value: Long = 1L) {
    fun loop(): Key = Key(subjectNumber, loops+1, (value*subjectNumber)%20201227L)
}