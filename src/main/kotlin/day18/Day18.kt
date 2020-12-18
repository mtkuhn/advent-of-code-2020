package day18

import java.io.File

fun main() {
    part1()
    part2()
}

data class HalfExpression(var left: Long, var operation: Char)

fun String.evaluateWithNoOrderOfOperations(): Long {
    val stack = mutableListOf<HalfExpression>()
    var currentExpression = HalfExpression(0L, '+')
    this.forEach { char ->
        if(char.isDigit()) applyOperation(currentExpression, char.toString().toLong())
        else if(char == '*' || char == '+') currentExpression.operation = char
        else if(char == '(') {
            stack += currentExpression
            currentExpression = HalfExpression(0L, '+')
        }
        else if(char == ')') currentExpression = applyOperation(stack.removeLast(), currentExpression.left)
    }
    return currentExpression.left
}

fun String.evalAllParenthesisOld() {
    " \\([^)]+\\) ".toRegex().replace(this) {
        val cleaned = it.value.trim()
        cleaned.evaluateWithOrderOfOperations()
    }
}

fun String.evalAllParenthesis(): String {
    var s = this
    while(s.contains("(")) {
        var openCount = 1
        var startPos = s.indexOf("(")
        var pos = startPos
        while(openCount != 0 && startPos < s.length) {
            pos++
            if(s[pos] == '(') openCount++
            if(s[pos] == ')') openCount--
        }
        val contents = " ${s.substring(startPos+1 until pos)} "
        val before = s.substring(0 until startPos-1)
        val after = s.substring(pos+2)
        s = before+contents.evaluateWithOrderOfOperations()+after
    }
    return s
}

fun String.evalOperations(operator: Char, operation: (Long, Long) -> Long): String {
    var r = this
    while(r.trim().any { it == operator }) {
        r = r.iterateOperations(operator, operation)
    }
    return r
}

fun String.iterateOperations(operator: Char, operation: (Long, Long) -> Long): String =
        " \\d+ [$operator] \\d+ ".toRegex().replace(this) {
            val a = it.value.substringBefore(operator).trim().toLong()
            val b = it.value.substringAfter(operator).trim().toLong()
            " ${operation.invoke(a, b)} "
        }

fun String.evaluateWithOrderOfOperations(): String {
    return evalAllParenthesis()
            .evalOperations('+') { a, b -> a + b }
            .evalOperations('*') { a, b -> a * b }
}

fun applyOperation(expression: HalfExpression, rightValue: Long): HalfExpression {
    when(expression.operation) {
        '+' -> expression.left += rightValue
        '*' -> expression.left *= rightValue
    }
    return expression
}

fun part1() {
    File("src/main/resources/day18_input.txt").readLines()
            .map { it.evaluateWithNoOrderOfOperations() }
            .sum()
            .apply { println(this) }
} //69490582260

fun part2() {
    File("src/main/resources/day18_input.txt").readLines()
            .map { " $it "}
            .map { it.evaluateWithOrderOfOperations().trim().toLong() }
            .sum()
            .apply { println(this) }
} //362464596624526