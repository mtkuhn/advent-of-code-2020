# Day 5: Binary Boarding.
## Part 1
### Problem
Using input encoded using binary space partitioning, determine the seat locations.
'F' and 'B' are used to divide into halves, from 0 to 127 in total.
'L' and 'R' are used to divide horizontally, between 0 and 7.
Determine the highest seat id, where the id is the row*8 plus the column.
### Solution
Each character in the input represents a number to be added to the total, which decreases
after each iteration. This number is either 0 or the full amount for that iteration.
It starts at 63 and is cut in half each time. Therefore, this can be done as a fold.
(I'm really digging fold and reduce the past few days.)
```
fun String.findBinaryPartitionValue(maxValue: Int, highChar: Char): Int =
    this.foldIndexed(0) { idx, seat, char ->
        seat + if(char == highChar) maxValue/2.pow(idx+1) else 0
    }
```
With this function, it's just some simple mapping and aggregate function to get our solution.