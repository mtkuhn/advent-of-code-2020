# Day 2
## Part 1
### Problem
Input contains a password policy alongside a password. Find the passwords that meet the corresponding policies.
> Each line gives the password policy and then the password. The password policy indicates the lowest and highest number of times a given letter must appear for the password to be valid. For example, 1-3 a means that the password must contain a at least 1 time and at most 3 times.
### Solution
There's only a few steps here:
1. Parse the line into usable values. For readability, I opted to parse each line into a data 
class and stored the min and max as an IntRange.
2. Validate each parsed line. This was a simple comparison of the char count to the `IntRange`.

        val count = lines.map { it.split("-", " ", ": ") }
            .map { split -> PolicyAndPassword(split[0].toInt(), split[1].toInt(), split[2][0], split[3]) }
            .count { pp -> pp.password.count { it == pp.letter } in pp.min..pp.max }

## Part 2
### Problem
Same as part 1, but with a new specification for how to interpret the policy:
> Each policy actually describes two positions in the password, where 1 means the first character, 2 means the second character, and so on. (Be careful; Toboggan Corporate Policies have no concept of "index zero"!) Exactly one of these positions must contain the given letter. Other occurrences of the letter are irrelevant for the purposes of policy enforcement.
### Solution
My part 1 design fit with this fairly well. I opted to refactor the data class
slightly to use a min and max value rather than range, as it makes usage for part 2
more clear. Other than that I simply had to update my evaluation function to use
the data differently.