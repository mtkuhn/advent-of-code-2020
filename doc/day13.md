# Day 13: Shuttle Search
[view source](/src/main/kotlin/day13/Day13.kt)
## Part 1
### Problem
With input of buses all leaving at time 0 and completing a loop every X minutes,
find the bus that will arrive first after a given starting time. The answer is the time
you have to wait multiplied by the frequency of that bus.
### Solution
This part seemed fairly simple to me. Take the modulo of the starting time by each bus's frequency,
which gives you the arrival times just *before* the start. Subtract those times from the bus's
frequency to flip it around to the *after* times. Then sort to find the answer.
```
    val goal = inputLines[0].toInt()
    inputLines[1].split(",")
            .mapNotNull { it.toIntOrNull() }
            .map { id -> id to id - goal%id }
            .minByOrNull { it.second }
            .let { (it?.first?:0) * (it?.second?:0) }
            .apply { println(this) }
}
```
## Part 2
### Problem
>Find the earliest timestamp such that the first bus ID departs at that time and each subsequent listed bus ID departs at that subsequent minute.
### Solution
I'm not sure if I can sufficiently explain this one. The rough idea is to start with
one bus frequency and offset(the index), find how it aligns to the next pair, then iterate
that alignment with the next values until done.
```
fun part2() {
    //pairs are (offset to frequency)
    val buses = inputLines[1].split(",")
            .map { it.toLongOrNull() }
            .mapIndexedNotNull { idx, it ->
                if(it != null) BusSchedule(idx.toLong(), it)
                else null
            }

    var acc = buses[0]
    buses.drop(1).forEach() { bus ->
        acc = findAlignmentOf(bus, acc)
    }
    println(acc.offset)
}

data class BusSchedule(val offset: Long, val freq: Long)
```
For each operation of two buses we do a lot of tricks. Each new alignment allows us
to skip values according to previous frequencies that have been accounted for.
```
fun findAlignmentOf(bus: BusSchedule, acc: BusSchedule): BusSchedule =
        generateSequence(acc.offset) { it + acc.freq } //skip by our accumlated freq to save time
                .first { i -> (bus.offset + i) % bus.freq == 0L } //meets critera
                .let { BusSchedule(it, bus.freq*acc.freq) } //produce new aligned schedule
```

This one was rough for me. My initial attempts at this were actually really close
to a solution, but in the end I had some bad assumptions and had to re-do it. My first
attempt involved breaking things down in a binary fashion and build up the answer from
a sum of parts. This was my mistake as it caused me to miss the lowest possible value and
jump ahead to a larger answer. One I accumulated the answer sequentially it worked out better.
