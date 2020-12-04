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

### Part 2
To solve for different down amounts in the slope was simple, I just filtered out rows using a mod on the index.

I refactored the tree calculation into its own function so that I could iterate over a list of slopes.

Finally, I used a `reduce` to calculate the product. I had some issues here as I initially got overflow and had to convert to BigInt.
(Later I realized `Long` was enough.)

## Day 4
### Part 1
The hardest part of this for me was figuring out how to parse the input data.
The fact that passwords could be spread across multiple lines meant that I needed
to combine those lines in a meaningful way. I came up with this:

    .fold(mutableListOf("")) { list, line  ->
        if(line.isBlank()) list.add("")
        if(list.last().isBlank()) list[list.lastIndex] += line
        else list[list.lastIndex] += " $line"
        list
    }

`fold` is much like `reduce`, except that in this case I can give it an intial value
for the accumulator. By making the accumulator a `List` I can selectively fold each new
element into either the last list item or as a new list item.

Once I had the data sorted per passport, it was a simple matter of creating a map of
values and checking for their existence.

### Part 2

All that was needed here was to add some additional validation. For some multi-step
validations I created extension functions on `String`. Most validation was done
using regex.