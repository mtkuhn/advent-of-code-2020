# Day 18: Operation Order
[view source](/src/main/kotlin/day18/Day18.kt)
## Problem
### Part 1
Given an input that consists of numbers, `*`, `+`, and `()`, such as `1 + 2 * 3 + (4 * 5) + 6`,
solve for the answer by evaluation left-to-right. Parenthesis are evaluated first
but no order is enforced between addition and multiplication.
### Part 2
Same, but now evaluate addition before multiplication.
## Solution
My first iteration on this involved defining a stack and working through the
operations in a state-machine-like way, but this fell apart quickly in
part 2. I hacked together an ugly mess for part 2, but came back later to
refactor into the following solution.

First, I want to parse the input into a series of objects. So I define the
following. Operands can be constant numbers or expressions(items within parentheses).
Operations are either addition or multiplication, and apply to an operand, for example
`+5`, `*4`, or `+(1+2)` are examples of operations.

```
interface Operand {
    fun evaluateLeftToRight(): Long
    fun evaluateAdditionFirst(): Long
}
data class Constant(val amount: Long): Operand {
    override fun evaluateLeftToRight(): Long = amount
    override fun evaluateAdditionFirst(): Long = amount
}
data class Expression(val operations: MutableList<Operation>): Operand {
    //todo
}

open class Operation(val operand: Operand)
class Addition(operand: Operand): Operation(operand)
class Multiplication(operand: Operand): Operation(operand)
```

With this in mind, we can start parsing the input. First I want to clean
things up and get them to more closely resemble the operations as I am
defining them. I get rid of spaces and add in all of the implied `+` signs.
```
fun List<String>.standardizeExpressionInput() =
        this.map { it.replace(" ", "") }
                .map { "+$it" }
                .map { "[^+*][\\d]+".toRegex().replace(it) { mr -> "${mr.value.take(1)}+${mr.value.drop(1)}" } }
                .map { "[^+*]\\(+".toRegex().replace(it) { mr -> "${mr.value.take(1)}+${mr.value.drop(1)}" } }
```

Then I parse that input into objects. We take remove off of the input
until we form a full operation or expression, stringing them together
as a list of successive operations.
```
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
```
With our data now well-defined, the rest of the work is the evaluation
of the expressions and operations.

For left-to-right, it's simply a successive accumulation of values in the list.
We recurse for expressions.
```
override fun evaluateLeftToRight(): Long =
        operations.fold(0L) { acc, op ->
            when (op) {
                is Addition -> acc+op.operand.evaluateLeftToRight()
                is Multiplication -> acc*op.operand.evaluateLeftToRight()
                else -> error("Unknown operation")
            }
        }
```

For order of operations (part 2), it's more complex. We stack consecutive additions in separate lists
then evaluate those individual lists. Then the only operation left is 
multiplication, so we simply multiply what's remaining left-to-right.
```
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
```
