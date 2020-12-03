# Advent of Code
Matt Kuhn 2020
<br>Implemented in Kotlin

## Day 1
### Part 1
A quick non-optimized solution for finding the numbers that add up to 2020.
I simply use a stream to iterate over the values, comparing each to the remaining values until I find one that satisfies the condition.

### Part 2
Though I could have simplified this to always look for exactly 3 numbers, 
I opted to generalize it to run for any number of values desired. 
This solution replaces my part 1 code completely, as the same code works in both cases. In theory,
I could run this for 4 values that sum to 2020, though no solution exists in that case.

It works by iteratively doing a breadth-wise expansion of options, 
filtering out impossibilities as it goes. There are likely faster ways to traverse the
tree, but this works well enough for the given dataset.

## Day 2
### Part 1
There's only a few steps here:
1. Parse the line into usable values. For readability, I opted to parse each line into a data 
class and stored the min and max as an IntRange.
2. Validate each parsed line. This was a simple comparison of the char count to the IntRange.

### Part 2
My part 1 design fit with this fairly well. I opted to refactor the data class
slightly to use a min and max value rather than range, as it makes usage for part 2
more clear. Other than that I simply had to update my evaluation function to use
the data differently.

## Day 3
### Part 1
Like the other puzzles, this can be solved easily with streams.
I first convert the data into a 2d array. 
I could have left it as a list of strings, but this just felt right.

Then I did an indexed map to take each line at a time, moving 3 positions for each line.
I mod the index by the width of the input to effectively loop it around rather than go off the edge.
Finally, I take a count of '#' characters found.