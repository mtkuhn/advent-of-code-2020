# Day 12: Rain Risk
[view source](/src/main/kotlin/day12/Day12.kt)
## Part 1
### Problem
Find the Manhattan Distance of a ship's final position from the starting position 
after following a list of navigation instructions.
>The navigation instructions (your puzzle input) consists of a sequence of single-character actions paired with integer input values
> * Action N means to move north by the given value.
> * Action S means to move south by the given value.
> * Action E means to move east by the given value.
> * Action W means to move west by the given value.
> * Action L means to turn left the given number of degrees.
> * Action R means to turn right the given number of degrees.
> * Action F means to move forward by the given value in the direction the ship is currently facing.
>
>The ship starts by facing east. Only the L and R actions change the direction the ship is facing.

### Solution
My first solution on this was attempted using `Double` values and angles recorded in Radians,
with lots of trigonometry to calculate vectors. But a little way into that I realized that
these puzzles don't tend to use such precision and I noticed the input only turns objects
by 90 degrees at a time. This gets rid of all trig functions and allows for a simpler solution.

First, some consts and convenience functions that will be useful. Most pairs I use will represent
and North/South by East/West value. In some contexts it will be a distance by direction (in degrees).
Here I can map angles to coordinates simply due to the 90-degree limitation.
```
const val EAST: Long = 0L
const val SOUTH: Long = 90L
const val WEST: Long = 180L
const val NORTH: Long = 270L

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
```
Now, I want a class to track the state of the boat and it's position.
```
class NavigationState(
        private var shipDirection: Long = 0, //in degrees, 0 is due East
        private var shipPosition: Pair<Long, Long> = 0L to 0L, //north to East
)
```
Using these, I can define function to accomplish what needs to be done for the various
instructions.
```
    fun moveShipByDistanceAndAngle(distance: Long, degrees: Long) {
        shipPosition += (distance to degrees).distanceAndAngleToCoordinates()
    }

    fun moveShipInCurrentDirection(distance: Long) =
            moveShipByDistanceAndAngle(distance, shipDirection)

    fun turnShip(degrees: Long) {
        shipDirection = Math.floorMod(shipDirection + degrees, 360L)
    }

    fun manhattanDistance(): Long =
            abs(shipPosition.first) + abs(shipPosition.second)
```
Then we just map our input to functions and iterate:
```
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
```

## Part 2
### Problem
Same situation, new interpretation of instructions.
> * Action N means to move the waypoint north by the given value.
> * Action S means to move the waypoint south by the given value.
> * Action E means to move the waypoint east by the given value.
> * Action W means to move the waypoint west by the given value.
> * Action L means to rotate the waypoint around the ship left (counter-clockwise) the given number of degrees.
> * Action R means to rotate the waypoint around the ship right (clockwise) the given number of degrees.
> * Action F means to move forward to the waypoint a number of times equal to the given value.

### Solution
First we need to update our class to account for the waypoint.
```
class NavigationState(
        private var shipDirection: Long = 0, //in degrees, 0 is due East
        private var shipPosition: Pair<Long, Long> = 0L to 0L, //north to East
        private var waypointPosition: Pair<Long, Long> = 1L to 10L
)
```
Like before, we create functions to handle the instructions. Of note is the rotation
function. Each 90-degree turn transposes the coordinates and flips one negative, so
it was easy to enumerate each option.
```
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
```
Finally, parse to the new instruction set.
```
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
```


