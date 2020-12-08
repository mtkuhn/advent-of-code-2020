package day8

import java.io.File

fun main() {
    part2()
}

fun part1() {
    val program = Program(getInputAsMapOfInstructions())
    program.run()
    println(program.accumulator)
}

fun part2() {
    val haltingInstructionHook: (Instruction) -> Boolean = { ins: Instruction ->
        !ins.isChecked && ins.operation in listOf("jmp", "nop")
    }
    val instructionConverter: (Instruction) -> Instruction = {
        it.copy(operation = if(it.operation == "jmp") "nop" else "jmp")
    }

    var program = Program(getInputAsMapOfInstructions()).apply { run(haltingInstructionHook) }
    while(program.state != ProgramState.COMPLETED) {
        val alteredProgram = program.deepCopy()
                .apply {
                    replaceInstruction(program.currentLine, instructionConverter)
                    run()
                }

        program = if(alteredProgram.state == ProgramState.COMPLETED) {
            alteredProgram
        } else {
            program.apply {
                markCurrentInstructionAsChecked()
                run(haltingInstructionHook)
            }
        }
    }
    println(program.accumulator)
}

fun getInputAsMapOfInstructions() =
        File("src/main/resources/day8_input.txt").readLines()
                .mapIndexed { idx, line ->
                    idx to Instruction(line.substringBefore(" "), line.substringAfter(" ").toInt())
                }.toMap().toMutableMap()

data class Instruction(val operation: String,
                       val argument: Int,
                       var isInvoked: Boolean = false,
                       var isChecked: Boolean = false)

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

    fun markCurrentInstructionAsChecked() {
        instructions[currentLine]?.isChecked = true
    }

    private fun invokeInstruction(instruction: Instruction?): Instruction? {
        if(instruction == null) error("null instruction encountered")

        instruction.isInvoked = true

        return when(instruction.operation) {
            "nop" -> next()
            "acc" -> { acc(instruction.argument); next() }
            "jmp" -> jmp(instruction.argument)
            else -> error("invalid instruction: $instruction")
        }
    }

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