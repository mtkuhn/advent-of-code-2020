package day17

fun main() {
    part1()
    part2()
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

fun part2() {
    var cubeMap = CubeMap4D()
    getInitialCubes().lines().mapIndexed { y, line ->
        line.mapIndexed { x, char ->
            if(char == '#') {
                cubeMap[Coordinate4D(x, y, 0, 0)] = true
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

data class Coordinate4D(val x: Int, val y: Int, val z: Int, val w: Int)

class CubeMap4D(
        private val map: MutableMap<Int, MutableMap<Int, MutableMap<Int, MutableMap<Int, Boolean>>>> = mutableMapOf(),
        private val default: Boolean = false
) {

    operator fun get(c: Coordinate4D): Boolean = map[c.x]?.get(c.y)?.get(c.z)?.get(c.w)?:default

    operator fun set(c: Coordinate4D, value: Boolean) {
        if(map[c.x] == null) map[c.x] = mutableMapOf()
        if(map[c.x]?.get(c.y) == null) map[c.x]?.set(c.y, mutableMapOf())
        if(map[c.x]?.get(c.y)?.get(c.z) == null) map[c.x]?.get(c.y)?.set(c.z, mutableMapOf())
        map[c.x]?.get(c.y)?.get(c.z)?.set(c.w, value)
    }

    fun getActiveCoordinates() =
            map.entries.flatMap { xEntry ->
                xEntry.value.entries.flatMap { yEntry ->
                    yEntry.value.entries.flatMap { zEntry ->
                        zEntry.value.entries.map { wEntry ->
                            Coordinate4D(xEntry.key, yEntry.key, zEntry.key, wEntry.key)
                        }
                    }
                }
            }

    private fun getNeighborsOf(c: Coordinate4D): List<Coordinate4D> =
            (-1..1).flatMap { x1 ->
                (-1..1).flatMap { y1 ->
                    (-1..1).flatMap { z1 ->
                        (-1..1).mapNotNull { w1 ->
                            if (x1 != 0 || y1 != 0 || z1 != 0 || w1 != 0)
                                Coordinate4D(c.x + x1, c.y + y1, c.z + z1, c.w + w1)
                            else null
                        }
                    }
                }
            }

    fun applyRules(): CubeMap4D {
        val newMap = CubeMap4D()
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