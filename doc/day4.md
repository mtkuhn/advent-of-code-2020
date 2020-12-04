# Day 4
## Part 1
### Problem
From an input file of passport fields, validate that each passport contains all required fields.
The fields are in a format of `key:value`, delimited by `space` or `newline`. New passports begin
after a blank line.

### Solution
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

## Part 2
### Problem
Validate the data for each value is valid, according to these rules:
> byr (Birth Year) - four digits; at least 1920 and at most 2002.
 <br>iyr (Issue Year) - four digits; at least 2010 and at most 2020.
 <br>eyr (Expiration Year) - four digits; at least 2020 and at most 2030.
 <br>hgt (Height) - a number followed by either cm or in:
 <br>If cm, the number must be at least 150 and at most 193.
 <br>If in, the number must be at least 59 and at most 76.
 <br>hcl (Hair Color) - a # followed by exactly six characters 0-9 or a-f.
 <br>ecl (Eye Color) - exactly one of: amb blu brn gry grn hzl oth.
 <br>pid (Passport ID) - a nine-digit number, including leading zeroes.
 <br>cid (Country ID) - ignored, missing or not.
### Solution

All that was needed here was to add some additional validation. For some multi-step
validations I created extension functions on `String`. Most validation was done
using regex.