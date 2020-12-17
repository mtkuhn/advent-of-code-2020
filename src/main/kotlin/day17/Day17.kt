package day17

fun main() {
    part1()
    part2()
}

fun getInitialInput() =
        """
            .#.##..#
            ....#.##
            ##.###..
            .#.#.###
            #.#.....
            .#..###.
            .#####.#
            #..####.
        """.trimIndent()

fun part1() {
    var cubeMap = AnyDimensionalMap(3)
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

interface DimensionalMap {
    operator fun get(c: List<Int>): Boolean
    operator fun set(c: List<Int>, newValue: Boolean)
    fun getActiveCoordinates(): List<List<Int>>
    fun getPointAndNeighbors(c: List<Int>): List<List<Int>>
}

//To be used as the base case in a N-dimensional map
class OneDimensionalMap(private val map: MutableMap<Int, Boolean> = mutableMapOf(), private val default: Boolean = false): DimensionalMap {
    override fun get(c: List<Int>): Boolean = map[c[0]]?:default
    override fun set(c: List<Int>, newValue: Boolean) { map[c[0]] = newValue }
    override fun getActiveCoordinates(): List<List<Int>> =
            map.entries.mapNotNull { if(it.value) listOf(it.key) else null }
    override fun getPointAndNeighbors(c: List<Int>): List<List<Int>> = listOf(c[0]-1, c[0], c[0]+1).map { listOf(it) }
}

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

}