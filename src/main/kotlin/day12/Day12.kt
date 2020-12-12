package day12

import java.io.File
import kotlin.math.*


fun main() {
    part1()
    part2()
}

fun getInstructionFromFileInput(): List<Pair<Char, Long>> =
        File("src/main/resources/day12_input.txt").readLines()
                .map { it[0] to it.substring(1).toLong() }

operator fun Pair<Long, Long>.plus(pos: Pair<Long, Long>) = first+pos.first to second+pos.second
operator fun Pair<Long, Long>.times(scale: Long) = first*scale to second*scale
fun Pair<Long, Long>.distanceAndAngleToCoordinates(): Pair<Long, Long> =
        when(this.second) {
            NavigationState.NORTH -> this.first to 0L
            NavigationState.SOUTH -> -this.first to 0L
            NavigationState.EAST -> 0L to this.first
            NavigationState.WEST -> 0L to -this.first
            else -> error("Does not support anything other than cardinal directions: ${this.second}")
        }

fun part1() {
    NavigationState().apply {
        getInstructionFromFileInput().forEach {
            when(it.first) {
                'N' -> moveShipByDistanceAndAngle(it.second, NavigationState.NORTH)
                'E' -> moveShipByDistanceAndAngle(it.second, NavigationState.EAST)
                'S' -> moveShipByDistanceAndAngle(it.second, NavigationState.SOUTH)
                'W' -> moveShipByDistanceAndAngle(it.second, NavigationState.WEST)
                'L' -> turnShip(-it.second)
                'R' -> turnShip(it.second)
                'F' -> moveShipInCurrentDirection(it.second)
                else -> error("Unsupported operation: $it")
            }
        }
        println(manhattanDistance())
    }
}

fun part2() {
    NavigationState().apply {
        getInstructionFromFileInput().forEach {
            when(it.first) {
                'N' -> moveWaypointInDirection(it.second, NavigationState.NORTH)
                'E' -> moveWaypointInDirection(it.second, NavigationState.EAST)
                'S' -> moveWaypointInDirection(it.second, NavigationState.SOUTH)
                'W' -> moveWaypointInDirection(it.second, NavigationState.WEST)
                'L' -> rotateWaypointAroundShip(-it.second)
                'R' -> rotateWaypointAroundShip(it.second)
                'F' -> moveShipTowardWaypoint(it.second)
                else -> error("Unsupported operation: $it")
            }
        }
        println(manhattanDistance())
    }
}

class NavigationState(
        private var shipDirection: Long = 0, //in degrees, 0 is due East
        private var shipPosition: Pair<Long, Long> = 0L to 0L, //north to East
        private var waypointPosition: Pair<Long, Long> = 1L to 10L
) {

    fun moveShipByDistanceAndAngle(distance: Long, degrees: Long) {
        shipPosition += (distance to degrees).distanceAndAngleToCoordinates()
    }

    fun moveShipInCurrentDirection(distance: Long) =
            moveShipByDistanceAndAngle(distance, shipDirection)

    fun turnShip(degrees: Long) {
        shipDirection = Math.floorMod(shipDirection + degrees, 360L)
    }

    fun moveWaypointInDirection(distance: Long, degrees: Long) {
        waypointPosition += (distance to degrees).distanceAndAngleToCoordinates()
    }

    fun moveShipTowardWaypoint(scale: Long) {
        shipPosition += waypointPosition*scale
    }

    fun rotateWaypointAroundShip(degrees: Long) {
        waypointPosition = when(Math.floorMod(degrees, 360)) {
            0L -> waypointPosition
            90L -> -waypointPosition.second to waypointPosition.first
            180L -> -waypointPosition.first to -waypointPosition.second
            270L -> waypointPosition.second to -waypointPosition.first
            else -> error("Does not support anything other than cardinal directions: $degrees")
        }
    }

    fun manhattanDistance(): Long =
            abs(shipPosition.first) + abs(shipPosition.second)

    companion object {
        const val EAST: Long = 0L
        const val SOUTH: Long = 90L
        const val WEST: Long = 180L
        const val NORTH: Long = 270L
    }
}