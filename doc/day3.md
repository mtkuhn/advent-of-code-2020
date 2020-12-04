# Day 3
## Part 1
### Problem
In a grid of chars (which loops horizontally), find how many tree characters we
will encounter when moving down 1, right 1.

### Solution
Like the other puzzles, this can be solved easily with streams.
I first convert the data into a 2d array. 
I could have left it as a list of strings, but this just felt right.

Then I did an indexed map to take each line at a time, moving 3 positions for each line.
I mod the index by the width of the input to effectively loop it around rather than go off the edge.
Finally, I take a count of '#' characters found.

## Part 2
### Problem
Same as above, but for additional slopes (down and right values).

### Solution
To solve for different down amounts in the slope was simple, I just filtered out rows using a mod on the index.

I refactored the tree calculation into its own function so that I could iterate over a list of slopes.

Finally, I used a `reduce` to calculate the product. I had some issues here as I initially got overflow and had to convert to BigInt.
(Later I realized `Long` was enough.)