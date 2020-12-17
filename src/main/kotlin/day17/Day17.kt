package day17

fun main() {
    part1()
}

fun getInitialCubes() =
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
    var cubeMap = CubeMap3D()
    getInitialCubes().lines().mapIndexed { y, line ->
        line.mapIndexed { x, char ->
            if(char == '#') {
                cubeMap[Triple(x, y, 0)] = true
            }
        }
    }

    repeat(6) { cubeMap = cubeMap.applyRules() }
    println(cubeMap.getActiveCoordinates().count())
}

class CubeMap3D(
        private val map: MutableMap<Int, MutableMap<Int, MutableMap<Int, Boolean>>> = mutableMapOf(),
        private val default: Boolean = false
) {

    operator fun get(c: Triple<Int, Int, Int>): Boolean = map[c.first]?.get(c.second)?.get(c.third)?:default

    operator fun set(c: Triple<Int, Int, Int>, value: Boolean) {
        if(map[c.first] == null) map[c.first] = mutableMapOf()
        if(map[c.first]?.get(c.second) == null) map[c.first]?.set(c.second, mutableMapOf())
        map[c.first]?.get(c.second)?.set(c.third, value)
    }

    private fun remove(c: Triple<Int, Int, Int>) { map[c.first]?.get(c.second)?.remove(c.third) }

    fun getActiveCoordinates() =
            map.entries.flatMap { xEntry ->
                xEntry.value.entries.flatMap { yEntry ->
                    yEntry.value.entries.map { zEntry ->
                        Triple(xEntry.key, yEntry.key, zEntry.key)
                    }
                }
            }

    private fun getNeighborsOf(c: Triple<Int, Int, Int>): List<Triple<Int, Int, Int>> =
            (-1..1).flatMap { x1 ->
                (-1..1).flatMap { y1 ->
                    (-1..1).mapNotNull { z1 ->
                        if (x1 != 0 || y1 != 0 || z1 != 0)
                            Triple(c.first+x1, c.second+y1, c.third+z1)
                        else null
                    }
                }
            }

    fun applyRules(): CubeMap3D {
        val newMap = CubeMap3D()
        getActiveCoordinates()
                .flatMap { getNeighborsOf(it) }.toSet()
                .forEach { c ->
                    val activeNeighbors = getNeighborsOf(c).count { this[it] }
                    if((!this[c] && activeNeighbors == 3)
                            || (this[c] && activeNeighbors in (2..3))) newMap[c] = true
                }
        return newMap
    }

}