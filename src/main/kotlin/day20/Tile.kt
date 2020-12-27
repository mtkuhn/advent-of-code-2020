package day20

fun main() {
    val testA = Tile("a", """
        12345
        ABCDE
        67890
        FGHIJ
        KLMNO
    """.trimIndent().lines())

    testA.print()
    testA.flippedHorizontally().print()
}

data class Tile(val id: String, val grid: List<String>) {

    fun flippedHorizontally(): Tile = Tile(id, grid.map { it.reversed() })

    fun rotateClockwise(): Tile =
            Tile(id, (grid.indices).mapIndexed { idx, _ ->
                grid.map { line ->
                    line[idx]
                }.reversed().joinToString(separator = "")
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

    fun toOrientation(orientation: Orientation): Tile {
        var tile = this
        repeat(orientation.clockwiseRotations) { tile = tile.rotateClockwise() }
        if(orientation.horizontalFlip) tile = tile.flippedHorizontally()
        return tile
    }

    fun print() {
        grid.forEach { println(it) }
    }

    fun getRow(row: Int): String = grid[row]
}