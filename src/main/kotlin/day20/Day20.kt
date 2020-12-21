package day20

import java.io.File

fun main() {
    part1()
}

enum class Direction {
    NORTH, SOUTH, EAST, WEST;

    fun opposite() =
            when(this) {
                NORTH -> SOUTH
                SOUTH -> NORTH
                EAST -> WEST
                WEST -> EAST
            }
}
data class Orientation(val clockwiseRotations: Int, val horizontalFlip: Boolean)

fun part1() {
    val tiles = File("src/main/resources/day20_input.txt").readLines()
            .chunked(12)
            .map { chunk ->
                val ref = chunk[0].drop(5).dropLast(1)
                Tile(ref, chunk.slice(1..10))
            }

    var tileGrids = listOf(TileGrid.emptyGrid())
    (0 until 144).forEach { idx ->
        val x = idx%12
        val y = idx/12
        tileGrids = tileGrids.flatMap { g -> findPossibleSolutionsForPosition(g, tiles, x, y) }
        println("possibilites=${tileGrids.count()}; fillCount=${tileGrids.first().countFilledTiles()}")
    }
    //tileGrids.first().print()
    //tileGrids.first().getCornerIds().apply { println(this) }
    //tileGrids.forEach { it.productOfCornerIds().apply { it.validate(tiles); println("corner product: $this") } }
    tileGrids.first().productOfCornerIds().apply { println("corner product: $this") }
    //3221,2029,1447,1873 -> 17712468069479


}

fun findPossibleSolutionsForPosition(tileGrid: TileGrid, tiles: List<Tile>, x: Int, y: Int): List<TileGrid> {
    return tileGrid.findTileOrientationsPossibleAt(tiles, x, y).map { tile ->
        tileGrid.copy().apply { this[x, y] = tile }
    }
}