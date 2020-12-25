package day25

fun main() {
    val pubKeyA = 3418282L
    val pubKeyB = 8719412L
    val subjectNumber = 7L

    val key = Key(subjectNumber)
    val keyA = key.loopUntilValue(pubKeyA)
    val keyB = key.loopUntilValue(pubKeyB)

    println(Key(keyB.value).loop(keyA.loops))
}

data class Key(val subjectNumber: Long, val loops: Int = 0, val value: Long = 1L) {

    fun loop(): Key = Key(subjectNumber, loops+1, (value*subjectNumber)%20201227L)

    fun loop(count: Int): Key {
        var key = this
        repeat(count) {
            key = key.loop()
        }
        return key
    }

    fun loopUntilValue(pubKey: Long): Key {
        var key = this
        while(key.value != pubKey) {
            key = key.loop()
        }
        return key
    }
}