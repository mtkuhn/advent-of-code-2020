package day18

import java.io.File

fun main() {
    part1()
    part2()
}

fun part1() {
    File("src/main/resources/day18_input.txt").readLines()
            .standardizeExpressionInput()
            .map { it.toMutableList().parseOperations() }
            .map { it.evaluateLeftToRight() }
            .sum().apply { println(this) }
} //69490582260

fun part2() {
    File("src/main/resources/day18_input.txt").readLines()
            .standardizeExpressionInput()
            .map { it.toMutableList().parseOperations() }
            .map { it.evaluateAdditionFirst() }
            .sum().apply { println(this) }
} //362464596624526

interface Operand {
    fun evaluateLeftToRight(): Long
    fun evaluateAdditionFirst(): Long
}
data class Constant(val amount: Long): Operand {
    override fun evaluateLeftToRight(): Long = amount
    override fun evaluateAdditionFirst(): Long = amount
}
data class Expression(val operations: MutableList<Operation>): Operand {
    override fun evaluateLeftToRight(): Long =
            operations.fold(0L) { acc, op ->
                when (op) {
                    is Addition -> acc+op.operand.evaluateLeftToRight()
                    is Multiplication -> acc*op.operand.evaluateLeftToRight()
                    else -> error("Unknown operation")
                }
            }

    override fun evaluateAdditionFirst(): Long  =
            operations
                    .fold(mutableListOf<MutableList<Operation>>(mutableListOf())) { acc, operation ->
                        if(operation is Addition) acc.last() += operation
                        else acc += mutableListOf(operation)
                        acc
                    }
                    .flatMap { opList ->
                        mutableListOf(Multiplication(Constant(
                                opList.fold(0L) { acc, op -> acc+op.operand.evaluateAdditionFirst() })))
                    }
                    .fold(1L) { acc, op -> acc*op.operand.evaluateAdditionFirst() }
}

open class Operation(val operand: Operand)
class Addition(operand: Operand): Operation(operand)
class Multiplication(operand: Operand): Operation(operand)

//strip out spaces, ensure everything starts with an operator (implied to be a +)
fun List<String>.standardizeExpressionInput() =
        this.map { it.replace(" ", "") }
                .map { "+$it" }
                .map { "[^+*][\\d]+".toRegex().replace(it) { mr -> "${mr.value.take(1)}+${mr.value.drop(1)}" } }
                .map { "[^+*]\\(+".toRegex().replace(it) { mr -> "${mr.value.take(1)}+${mr.value.drop(1)}" } }

fun MutableList<Char>.parseOperations(): Expression {
    val operations = mutableListOf<Operation>()
    while(this.isNotEmpty()) {
        operations += removeAndTakeOperation()
    }
    return Expression(operations)
}

fun MutableList<Char>.removeAndTakeExpression(): Expression {
    var openParentheses = 1
    val expressionString = this.drop(1).takeWhile { char ->
        if(char == '(') openParentheses++
        else if(char == ')') openParentheses--
        openParentheses != 0
    }.joinToString(separator = "")
    repeat(expressionString.length+2) { this.removeFirst() }
    return expressionString.toMutableList().parseOperations()
}

fun MutableList<Char>.removeAndTakeOperation(): Operation {
    val operator = this.removeFirst()
    val operand =
            when {
                this.first().isDigit() -> Constant(this.removeFirst().toString().toLong())
                this.first() == '(' -> this.removeAndTakeExpression()
                else -> error("Unexpected syntax in ${this.joinToString(separator = "")}")
            }
    return if(operator == '+') Addition(operand) else Multiplication(operand)
}