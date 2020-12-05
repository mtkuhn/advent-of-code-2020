# Day 5: Binary Boarding.
[view source](/src/main/kotlin/day5/Day5.kt)
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

## Part 2
### Problem
Find your seat number by process of elimination. 
Yours is the one missing in the input. Some seats won't exist, but ids `-1` and `+1` from yours will exist.
### Solution
I maybe could have brute-forced this by generating a list of valid ids and subtracting the known seats, but
I thought I'd go for something more performant. In the end I did the following:
1. Find all known seats and ids
2. Find rows that did not have all 8 columns filled
3. For the incomplete rows, determine the missing seat data
4. Filter missing seat candidates by checking the id before and after

```
val missingSeats = knownSeats.groupBy { it.row }
        .filter { it.value.size < 8 }
        .map { rowMapEntry -> rowMapEntry.key to (0..7)-rowMapEntry.value.map { it.col }.toList() }
        .flatMap { mapEntry ->
            mapEntry.second.map {
                Seat.fromPosition(mapEntry.first, it)
            }
        }.filter {
            knownSeatIds.contains(it.id+1) && knownSeatIds.contains(it.id-1)
        }
```