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