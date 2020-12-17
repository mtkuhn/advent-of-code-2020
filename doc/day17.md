# Day 17: Conway Cubes
[view source](/src/main/kotlin/day17/Day17.kt)
## Problem
Given the nature of parts 1 and 2, I'm listing both first.
### Part 1
With an input that is a plane in agrid of activated vs unactivated cubes in
3-dimensional space, apply a set of rules 6 times and count the new
number of activated cubes.
The rules are:
> * If a cube is active and exactly 2 or 3 of its neighbors are also active, the cube remains active. Otherwise, the cube becomes inactive.
> * If a cube is inactive but exactly 3 of its neighbors are active, the cube becomes active. Otherwise, the cube remains inactive.

### Part 2
Same thing, but with 4-dimensional space.

## Solution
My initial solution was to create a class to manage a 3D grid.
It was effectively a nice interface with convenience functions 
on top of a `Map<Int, Map<Int, Map<Int, Boolean>>>`. When part 
2 rolled around, I refactored this solution to go one more Map 
deep as a new 4D class. Finally, I merged these solutions into 
one N-dimensional class. The following is from that generic solution.

To start, I defined an interface for a grid and created a simple
1-dimensional implementation.
```
interface DimensionalMap {
    operator fun get(c: List<Int>): Boolean
    operator fun set(c: List<Int>, newValue: Boolean)
    fun getActiveCoordinates(): List<List<Int>>
    fun getPointAndNeighbors(c: List<Int>): List<List<Int>>
}

class OneDimensionalMap(private val map: MutableMap<Int, Boolean> = mutableMapOf(), private val default: Boolean = false): DimensionalMap {
    override fun get(c: List<Int>): Boolean = map[c[0]]?:default
    override fun set(c: List<Int>, newValue: Boolean) { map[c[0]] = newValue }
    override fun getActiveCoordinates(): List<List<Int>> =
            map.entries.mapNotNull { if(it.value) listOf(it.key) else null }
    override fun getPointAndNeighbors(c: List<Int>): List<List<Int>> = listOf(c[0]-1, c[0], c[0]+1).map { listOf(it) }
}
```
The N-dimensional implementation will rely upon the `OneDimensionalMap`
as a base case. Each N dimensional grid will do some work then delegate
the rest to a (N-1) dimensional grid, until the base case is found. 

You can see this outlined in how `get` and `set` operations work. The `set`
operation has logic to dynamically spin up new maps and entries as we 
access new space within the grid, and knows when to switch over to an
`AnyDimensionalMap`.
```
class AnyDimensionalMap(
        private val dimensions: Int,
        private val map: MutableMap<Int, DimensionalMap> = mutableMapOf(),
        private val default: Boolean = false
): DimensionalMap {

    override operator fun get(c: List<Int>): Boolean =
            map[c[0]]?.get(c.drop(1))?:default

    override operator fun set(c: List<Int>, newValue: Boolean) {
        if(map[c[0]] == null) {
            if(dimensions > 2) map[c[0]] = AnyDimensionalMap(dimensions-1)
            else map[c[0]] = OneDimensionalMap()
        }
        map[c[0]]?.set(c.drop(1), newValue)
    }
```
We need some functions to do some common operations. Again, `getActiveCoordinates()` traverses down the
Russian Doll of DimensionalMaps.
```
    override fun getActiveCoordinates(): List<List<Int>> =
            map.entries.flatMap { cEntry ->
                cEntry.value.getActiveCoordinates().map { lowerCoordinates ->
                    listOf(cEntry.key) + lowerCoordinates
                }
            }

    override fun getPointAndNeighbors(c: List<Int>): List<List<Int>> {
        var points = (-1..1).map { listOf(c[0]+it) }
        repeat(dimensions-1) { dimension ->
            points = points.flatMap { coordinate ->
                (-1..1).map { newPoint ->
                    coordinate + (c[dimension+1] + newPoint)
                }
            }
        }
        return points
    }


    private fun getNeighborsOf(c: List<Int>) = getPointAndNeighbors(c).filter { it != c }
```
Finally, my application of rules changed very little between versions, as the
operator-overloaded accessors hide most of the magic.
```
    fun applyRules(): AnyDimensionalMap {
        val newMap = AnyDimensionalMap(dimensions)
        getActiveCoordinates()
                .flatMap { getNeighborsOf(it) }.toSet()
                .forEach { c ->
                    val activeNeighbors = getNeighborsOf(c).count { this[it] }
                    if((!this[c] && activeNeighbors == 3) || (this[c] && activeNeighbors in (2..3)))
                        newMap[c] = true
                }
        return newMap
    }
```
Finally, we just repeat the desired number of rule applications on an appropriately dimensioned map.
```

fun part2() {
    var cubeMap = AnyDimensionalMap(4)
    getInitialInput().lines().mapIndexed { y, line ->
        line.mapIndexed { x, char ->
            if(char == '#') {
                cubeMap[listOf(x, y, 0, 0)] = true
            }
        }
    }

    repeat(6) { cubeMap = cubeMap.applyRules() }
    println(cubeMap.getActiveCoordinates().count())
}
```