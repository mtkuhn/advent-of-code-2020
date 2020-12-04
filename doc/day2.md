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