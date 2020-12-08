package day8

import java.io.File

fun main() {
    part1()
}

fun part1() {
    val program = Program(getInputAsMapOfInstructions())
    program.run()
    println(program.accumulator)
}

fun getInputAsMapOfInstructions() =
        File("src/main/resources/day8_input.txt").readLines()
                .mapIndexed { idx, line ->
                    idx to Instruction(line.substringBefore(" "), line.substringAfter(" ").toInt())
                }.toMap()

data class Instruction(val operation: String, val argument: Int, var invocationCount: Int = 0)

class Program(val instructions: Map<Int, Instruction>, var currentLine: Int = 0, var accumulator: Int = 0) {

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