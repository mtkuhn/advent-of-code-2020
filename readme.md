# Advent of Code
Matt Kuhn 2020
<br>Implemented in Kotlin

## Day 1
###Part 1
A quick non-optimized solution for finding the numbers that add up to 2020.
I simply use a stream to iterate over the values, comparing each to the remaining values until I find one that satisfies the condition.

###Part 2
Though I could have simplified this to always look for exactly 3 numbers, 
I opted to generalize it to run for any number of values desired. 
This solution replaces my part 1 code completely, as the same code works in both cases. In theory,
I could run this for 4 values that sum to 2020, though no solution exists in that case.

It works by iteratively doing a breadth-wise expansion of options, 
filtering out impossibilities as it goes. There are likely faster ways to traverse the
tree, but this works well enough for the given dataset.