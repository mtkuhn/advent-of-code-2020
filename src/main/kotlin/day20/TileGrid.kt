package day20

class TileGrid(val gridList: Array<Array<Tile?>>) {
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

    fun findFilledNeighborsOf(row: Int, col: Int): List<Pair<Direction, Tile>> =
            listOf(
                    Direction.EAST to this.tileAtOrNull(row, col+1),
                    Direction.WEST to this.tileAtOrNull(row, col-1),
                    Direction.NORTH to this.tileAtOrNull(row-1, col),
                    Direction.SOUTH to this.tileAtOrNull(row+1, col)
            ).mapNotNull { if(it.second != null) it as Pair<Direction, Tile> else null }

    fun tileAtOrNull(row: Int, col: Int): Tile? =
            if(gridList.lastIndex >= row && row >= 0 && gridList[row].lastIndex >= col && col >= 0)
                gridList[row][col] else null

    fun countFilledTiles() = gridList.map { it.filterNotNull().count() }.sum()

    fun getCornerIds(): List<Long> =
            listOf((this[0,0]?.id?.toLong()?:0L),(this[0,11]?.id?.toLong()?:0L),
                    (this[11,0]?.id?.toLong()?:0L),(this[11,11]?.id?.toLong()?:0L))

    fun productOfCornerIds(): Long =
        (this[0,0]?.id?.toLong()?:0L)*(this[0,11]?.id?.toLong()?:0L)*
                (this[11,0]?.id?.toLong()?:0L)*(this[11,11]?.id?.toLong()?:0L)

    fun print() {
        println("-".repeat(133))
        gridList.forEach { rowTiles ->
            (-1 until 10).forEach { rowNum ->
                val rowStr = rowTiles.map { t ->
                    if(rowNum == -1) {
                        "-id=${t?.id}--"
                    } else {
                        t?.getRow(rowNum)?:"-----------"
                    }
                }.joinToString(separator = "|", prefix = "|", postfix="|")
                println("$rowStr")
            }
            println("-".repeat(133))
        }
    }

    fun toBigPicture(): List<String> =
            gridList.flatMap { rowTiles ->
                (0 until 10).map { rowNum ->
                    rowTiles.map { t ->
                            t?.getRow(rowNum)?:"-----------"
                    }.joinToString(separator = "")
                }
            }

    fun validate(tiles: List<Tile>): Boolean {
        val ids = this.gridList.flatten()
        if(ids.size != 144) {
            println("Invalid grid: Not filled. size=${ids.size}")
            return false
        }

        val distinctIds = ids.map { it?.id?:"x" }.distinct()
        if(distinctIds.count() != 144) {
            println("Invalid grid: Ids are not distinct. count=${distinctIds.count()}")
            return false
        }

        gridList.forEach { row ->
            (0..10).forEach { index ->
                val east = row[index]?.getEdgeInDirection(Direction.EAST)
                val west = row[index+1]?.getEdgeInDirection(Direction.WEST)
                if(east != west) {
                    println("Invalid grid: Rows don't align. rowIndex=${index}, row=${row}")
                    return false
                }
            }
        }

        (0..10).forEach { row ->
            (0..11).forEach { col ->
                val south = this[row, col]?.getEdgeInDirection(Direction.SOUTH)
                val north = this[row+1, col]?.getEdgeInDirection(Direction.NORTH)
                if(south != north) {
                    println("Invalid grid: Columns don't align. col=${col}, row=${row}")
                    return false
                }
            }
        }

        distinctIds.forEach { id ->
            val inputTile = tiles.find { it.id == id }
            if(inputTile == null) {
                println("Invalid grid: tile not contained in original input tile list. id={$id}")
            } else {
                //todo: tile is consistent with a valid orientation
            }
        }

        return true
    }

    companion object {
        fun emptyGrid(): TileGrid =
                (0..11).map {
                    arrayOfNulls<Tile>(12)
                }.toTypedArray().let { TileGrid(it) }
    }
}