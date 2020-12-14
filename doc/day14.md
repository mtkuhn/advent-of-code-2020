# Day 14: Docking Data
[view source](/src/main/kotlin/day14/Day14.kt)
## Part 1
### Problem
You are given input consisting of two types of lines. One of form
`mem[1] = 1234` which means to write a value to a memory location, and another
of form `mask = XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X`.
Values written to memory should first be masked using the latest mask value. The mask
is defined as such:
>a 0 or 1 overwrites the corresponding bit in the value, while an X leaves the bit in the value unchanged

Find the sum of memory values after running the instructions.
### Solution
I read in the input, applying as we go. Variables for the mask and memoryMap are stored outside
of the functional loop.

If we hit a `mem` command we write a masked value for that location into a mutable map.

I opted to take the mask input and split it into a pair of values. One holds the 0's for a bitwise `or`, the other
holds the 1's for a bitwise `and`. These operations will achieve the desired masking.
```
fun part1() {
    var mask = 0L to 0L
    val memoryMap = mutableMapOf<Long, Long>()
    getInput().map { it.split(" = ") }
            .forEach { s ->
                if(s[0] == "mask") mask = (s[1].replace("X", "0").toLong(2)
                        to s[1].replace("X", "1").toLong(2))
                else memoryMap[getMemoryInstructionPosition(s[0])] =
                        s[1].toLong() or mask.first and mask.second
            }

    memoryMap.values.sum().apply { println(this) }
}

fun getMemoryInstructionPosition(instructionString: String): Long =
        "mem\\[([0-9]+)]".toRegex().find(instructionString)?.groupValues?.get(1)?.toLong()?:-1
```
## Part 2
### Problem
New definition for the mask. Instead of making the value, it decodes the address
according to this scheme. The mask can cause a `mem` command to update many values.
> * If the bitmask bit is 0, the corresponding memory address bit is unchanged.
> * If the bitmask bit is 1, the corresponding memory address bit is overwritten with 1.
> * If the bitmask bit is X, the corresponding memory address bit is floating.
### Solution
First I needed some bit manipulation functions. These were initially very ugly
string manipulations, but I managed to clean them up to these:
```
fun getBitFromLeft(address: Long, i: Int): Boolean =
        (address shr i-1) and 1L == 1L

fun flipBitFromLeft(address: Long, i: Int): Long {
    val diff = (2.0).pow(i-1).toLong()
    val existingBit = getBitFromLeft(address, i)
    return if(existingBit) address - diff else address + diff
}
```
Then a function to decode them `mem` command addresses.
```
fun decodeMemoryAddress(mask: String, address: Long): List<Long> {
    val addresses = mutableListOf(mask.replace("X", "0").toLong(2) or address)
    mask.mapIndexedNotNull { idx, char -> if(char == 'X') idx else null }
            .forEach { i ->
                addresses.addAll(addresses.map { address -> flipBitFromLeft(address, 36-i) })
            }
    return addresses.toList()
}
```
Then pulling it all together similarly to part 1:
```
fun part2() {
    var mask = 0L.toString(2)
    val memoryMap = mutableMapOf<Long, Long>()
    getInput().map { it.split(" = ") }
            .forEach { s ->
                if(s[0] == "mask") mask = s[1]
                else decodeMemoryAddress(mask, getMemoryInstructionPosition(s[0]))
                        .forEach { a -> memoryMap[a] = s[1].toLong() }
            }

    memoryMap.values.sum().apply { println(this) }
}
```