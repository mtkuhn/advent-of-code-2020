# Day 20: Jurassic Jigsaw
[view source](/src/main/kotlin/day20/Day20.kt)
## Part 1
### Problem
Given a series of images (2d char arrays), assemble them together into a square image 
in such a way that the characters along the edges align. Each image can be rotated
and flipped.
The solution is the product of the tile ids for the corners.
### Solution
First, some setup. I want some classes to help track directions and orientations for
the tiles.
```
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
```
Then, I want classes for the grid and tiles. We'll be building these out.
```
data class Tile(val id: String, val grid: List<String>)

class TileGrid(val gridList: Array<Array<Tile?>>,
               val gridWidth: Int = Math.sqrt(gridList.flatten().size.toDouble()).toInt()) {
    
    operator fun get(x: Int, y:Int): Tile? = gridList[x][y]
    operator fun set(x: Int, y:Int, newValue: Tile) { gridList[x][y] = newValue }

    companion object {
        fun emptyGrid(width: Int): TileGrid =
                (0 until width).map {
                    arrayOfNulls<Tile>(width)
                }.toTypedArray().let { TileGrid(it) }
    }
}
```
The `Tile` class is there mostly to transform the data between orientations.
Of importance is the `getAllOrientations` function that takes a tile and gives us
every way we can flip or rotate it (8 in all).
```
    private fun flippedHorizontally(): Tile = Tile(id, grid.map { it.reversed() })

    private fun rotateClockwise(): Tile =
            Tile(id, (grid.indices).mapIndexed { idx, _ ->
                grid.map { line -> line[idx] }.reversed().joinToString(separator = "")
            }.toList())

    fun getAllOrientations(): List<Tile> =
            (0..3).flatMap { rot ->
                listOf(true, false).map { hFlip -> Orientation(rot, hFlip) }
            }.map { this.toOrientation(it) }

    fun getEdgeInDirection(direction: Direction): String =
            when(direction) {
                Direction.NORTH -> this.grid.first()
                Direction.SOUTH -> this.grid.last()
                Direction.EAST -> this.rotateClockwise().grid.last()
                Direction.WEST -> this.rotateClockwise().grid.first()
            }

    private fun toOrientation(orientation: Orientation): Tile {
        var tile = this
        repeat(orientation.clockwiseRotations) { tile = tile.rotateClockwise() }
        if(orientation.horizontalFlip) tile = tile.flippedHorizontally()
        return tile
    }

    fun getRow(row: Int): String = grid[row]
```
The `TileGrid` class a lot more complex business logic. Most importantly, it
can determine which of a list of tiles is possible at a given location in the grid.
```
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
```
It can also get the product of the corners to help us with the ultimate answer.
```
    fun productOfCornerIds(): Long =
        (this[0,0]?.id?.toLong()?:0L)*(this[0,gridWidth-1]?.id?.toLong()?:0L)*
                (this[gridWidth-1,0]?.id?.toLong()?:0L)*(this[gridWidth-1,gridWidth-1]?.id?.toLong()?:0L)
```
Finally, we bring it all together in the main method.
1. Read the input into Tile objects.
2. Init an empty grid.
3. For each grid slot, get possible tiles. For each of those possibilities, get
the possibilities for the next one, and so on.
4. Eventually we are left with the 8 orientations of the solution, take the
first and get the product of the corners.
```
fun main() {
    var width = 12
    val tiles = File("src/main/resources/day20_input.txt").readLines()
            .chunked(12)
            .map { chunk ->
                val ref = chunk[0].drop(5).dropLast(1)
                Tile(ref, chunk.slice(1..10))
            }

    var tileGrids = listOf(TileGrid.emptyGrid(width))
    (0 until width*width).forEach { idx ->
        val x = idx%width
        val y = idx/width
        tileGrids = tileGrids.flatMap { g -> findPossibleSolutionsForPosition(g, tiles, x, y) }
        println("possibilites=${tileGrids.count()}; fillCount=${tileGrids.first().countFilledTiles()}")
    }

    tileGrids.first().productOfCornerIds().apply { println("corner product: $this") }
}

fun findPossibleSolutionsForPosition(tileGrid: TileGrid, tiles: List<Tile>, x: Int, y: Int): List<TileGrid> {
    return tileGrid.findTileOrientationsPossibleAt(tiles, x, y).map { tile ->
        tileGrid.copy().apply { this[x, y] = tile }
    }
}
```
It's a slow solution, but it works. There are likely lots of optimizations to be made, 
but I took too long on this as-is.
## Part 1
### Problem
Take the resulting grid, chop off the borders, and combine into one image.
Then look for this guy (spaces are wildcards):
```
                  # 
#    ##    ##    ###
 #  #  #  #  #  #   
```
### Solution
First I converted my 8 solution grids into one large tile each, so that I can search
them easier.
```
    tileGrids.forEach {
        val bigPic = it.toBigPicture()
        val c = countSeaMonsters(bigPic)
        if (c != 0) {
            bigPic.forEach { l -> println(l) }
            println("monster count = $c")
            bigPic.map { s ->
                s.count { c -> c == '#' }
            }.sum().apply { println("water=$this") }
        }
    }
```
To do the actual search, I pre-calculate the coordinates for each
`#` in the sea monster so that I can use them as a sort of mask.

Then I iterate over each character looking for the mask to apply when
offset by that character's position.
```
fun countSeaMonsters(pic: List<String>): Int {
    val monster = """
                  # 
#    ##    ##    ###
 #  #  #  #  #  #   
    """.trimIndent().lines()

    val monsterMaskCoordinates = monster.flatMapIndexed { rowNum, row ->
        row.mapIndexedNotNull { colNum, char ->
            if(char == '#') rowNum to colNum
            else null
        }
    }

    return (pic.indices).map { row ->
        (pic[row].indices).map { col ->
            monsterMaskCoordinates.map { mm ->
                val x = mm.first+row
                val y = mm.second+col
                if(x in pic.indices && y in pic[0].indices) pic[x][y]
                else '!'
            }.all { it == '#' }
        }.count { it }
    }.sum()
}
```

