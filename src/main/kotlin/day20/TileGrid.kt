package day20

import kotlin.math.sqrt

class TileGrid(private val gridList: Array<Array<Tile?>>,
               private val gridWidth: Int = sqrt(gridList.flatten().size.toDouble()).toInt()) {
    operator fun get(x: Int, y:Int): Tile? = gridList[x][y]
    operator fun set(x: Int, y:Int, newValue: Tile) { gridList[x][y] = newValue }

    fun copy(): TileGrid = TileGrid(gridList.map { it.copyOf() }.toTypedArray())

    fun findTileOrientationsPossibleAt(allTiles: List<Tile>, row: Int, col: Int): List<Tile> {
        val gridTileIds = gridList.flatten().mapNotNull { t -> t?.id }
        return allTiles.filter { !gridTileIds.contains(it.id) }
                .flatMap { possibleTile ->
                    possibleTile.getAllOrientations().filter { possibleTileOrientation ->
                        this.findFilledNeighborsOf(row, col).all { neighbor ->
                            possibleTileOrientation.getEdgeInDirection(neighbor.first) ==
                                    neighbor.second.getEdgeInDirection(neighbor.first.opposite())
                        }
                    }
                }
    }

    private fun findFilledNeighborsOf(row: Int, col: Int): List<Pair<Direction, Tile>> =
            listOf(
                    Direction.EAST to this.tileAtOrNull(row, col+1),
                    Direction.WEST to this.tileAtOrNull(row, col-1),
                    Direction.NORTH to this.tileAtOrNull(row-1, col),
                    Direction.SOUTH to this.tileAtOrNull(row+1, col)
            ).mapNotNull { if(it.second != null) it as Pair<Direction, Tile> else null }

    private fun tileAtOrNull(row: Int, col: Int): Tile? =
            if(gridList.lastIndex >= row && row >= 0 && gridList[row].lastIndex >= col && col >= 0)
                gridList[row][col] else null

    fun countFilledTiles() = gridList.map { it.filterNotNull().count() }.sum()

    fun productOfCornerIds(): Long =
        (this[0,0]?.id?.toLong()?:0L)*(this[0,gridWidth-1]?.id?.toLong()?:0L)*
                (this[gridWidth-1,0]?.id?.toLong()?:0L)*(this[gridWidth-1,gridWidth-1]?.id?.toLong()?:0L)

    fun toBigPicture(): List<String> =
            gridList.flatMap { tileRow ->
                (1 until 9).map { inTileRow -> //skip last row, except at end
                    tileRow.joinToString(separator = "") { tile ->
                        tile!!.getRow(inTileRow).drop(1).dropLast(1)
                    }
                }
            }

    companion object {
        fun emptyGrid(width: Int): TileGrid =
                (0 until width).map {
                    arrayOfNulls<Tile>(width)
                }.toTypedArray().let { TileGrid(it) }
    }
}