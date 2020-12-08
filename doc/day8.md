# Day 8: Handheld Halting
[view source](/src/main/kotlin/day8/Day8.kt)
## Part 1
### Problem
You are given a list of assembly-ish instructions with one arg and one of the following instructions:
>acc increases or decreases a single global value called the accumulator by the value given in the argument. 
>After an acc instruction, the instruction immediately below it is executed next.

>jmp jumps to a new instruction relative to itself. 
>The next instruction to execute is found using the argument as an offset from the jmp instruction.

>nop stands for No OPeration - it does nothing. The instruction immediately below it is executed next.

Given the instruction input, find the value of the accumulator just before any instruction is executed more than once.

### Solution

Given the number of variables for things like the accumulator and tracking of the current line, 
I decided to go with a more Object-Oriented approach than I've done for AoC so far. First I defined an
`Instruction` and created a method to get my input as a `Map<Int, Instruction>` (the int is the line number).
Note that I included an invocation count on the instruction.

```
data class Instruction(val operation: String, val argument: Int, var invocationCount: Int = 0)

fun getInputAsMapOfInstructions() =
        File("src/main/resources/day8_input.txt").readLines()
                .mapIndexed { idx, line ->
                    idx to Instruction(line.substringBefore(" "), line.substringAfter(" ").toInt())
                }.toMap()
```

Once I had that, I could write a `Program` class to track the variables and perform operations on them.

```
class Program(val instructions: Map<Int, Instruction>, var currentLine: Int = 0, var accumulator: Int = 0) {

    private fun next(): Instruction? {
        currentLine += 1
        return instructions[currentLine]
    }

    private fun acc(amount: Int) { accumulator += amount }

    private fun jmp(amount: Int): Instruction? {
        currentLine += amount
        return instructions[currentLine]
    }

}
```

Then I only needed to loop over instructions.

```
    fun run() {
        while(instructions[currentLine]?.invocationCount == 0) {
            invokeInstruction(instructions[currentLine])
        }
    }

    private fun invokeInstruction(instruction: Instruction?): Instruction? {
        if(instruction == null) error("null instruction encountered")

        instruction.invocationCount += 1

        return when(instruction.operation) {
            "nop" -> next()
            "acc" -> { acc(instruction.argument); next() }
            "jmp" -> jmp(instruction.argument)
            else -> error("invalid instruction: $instruction")
        }
    }
```

Then, simply run the program and grab the result!
```
    val program = Program(getInputAsMapOfInstructions())
    program.run()
    println(program.accumulator)
```