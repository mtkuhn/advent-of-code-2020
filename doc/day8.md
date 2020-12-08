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

Given the instruction input, find the value of the accumulator just before any instruction executed more than once.

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
        while(currentLine < instructions.size) {
            if(instructions[currentLine]?.invocationCount != 0) {
                break
            }
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

## Part 2
### Problem
The program can be fixed not fall into an infinite loop by either changing one `jmp` to `nop`
or one `nop` to `jmp`. Find the one instruction to change for the fix and print the value of the
accumulator after it runs with this fix.
### Solution
Now it's getting interesting! The name of the game here is to programmatically alter the input and
test it for full-completion vs infinite-loop. I'm also going to make a needless optimization and
have it never repeat the full instruction set more than once.

First I need to update my Program class with a `state` and define an enum for those states. It's
now very important I know why it is that I'm stopping execution.
```
enum class ProgramState { COMPLETED, INFINITE_LOOP, HALTED }

class Program(private val instructions: MutableMap<Int, Instruction>,
              var state: ProgramState = ProgramState.HALTED,
              var currentLine: Int = 0,
              var accumulator: Int = 0) {

    fun run(haltingInstructionHook: (Instruction) -> Boolean = { false }) {
        while(currentLine < instructions.size) {
            val ins = instructions[currentLine] ?: error("Invalid instruction at line $currentLine")
            if(ins.isInvoked) {
                state = ProgramState.INFINITE_LOOP
                break
            }
            if(haltingInstructionHook.invoke(ins)) {
                state = ProgramState.HALTED
                break
            }
            invokeInstruction(ins)
        }
        if(currentLine == instructions.size) state = ProgramState.COMPLETED
    }
```

You might notice that I also added a hook that can help me out when we hit a `jmp` or `nop`.
The gist is that we want to pause and mess with the data when he hit those. 
I also snuck in a new property for `Instruction.isChecked`, this allows us to bypass the hook when
necessary. Here's what the hook ends up being:
```
    val haltingInstructionHook: (Instruction) -> Boolean = { ins: Instruction ->
        !ins.isChecked && ins.operation in listOf("jmp", "nop")
    }
```
Now we need some class methods to manipulate the data, including a deep copy
so that we don't mix up our results.
```
    fun deepCopy(): Program {
        val newInstructions = instructions.map { it.key to it.value.copy() }.toMap().toMutableMap()
        return Program(newInstructions, this.state, this.currentLine, this.accumulator)
    }

    fun replaceInstruction(replaceLine: Int, instructionConverter: (Instruction) -> Instruction) {
        val instructionToUpdate = instructions[replaceLine]
        if(instructionToUpdate != null) {
            instructions[replaceLine] = instructionConverter.invoke(instructionToUpdate)
        }
    }
```
And you may have noticed there's another high-order function involved in the above:
```
    val instructionConverter: (Instruction) -> Instruction = {
        it.copy(operation = if(it.operation == "jmp") "nop" else "jmp")
    }
```
Given this setup, we can now evaluate as result. I run until we halt (at a `jmp` or `nop`), then test a run
against an altered instruction. If it runs to completion we have our answer, otherwise we move on and keep testing
until an answer does arrive.
```
    var program = Program(getInputAsMapOfInstructions()).apply { run(haltingInstructionHook) }
    while(program.state != ProgramState.COMPLETED) {
        program = evalWithAlteredInstruction(program, instructionConverter)
                ?:program.apply {
                    markCurrentInstructionAsChecked()
                    run(haltingInstructionHook)
                }
    }
    println(program.accumulator)
```