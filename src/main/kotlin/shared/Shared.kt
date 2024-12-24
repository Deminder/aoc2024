package com.github.deminder.shared

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.util.*

typealias Vec2 = Pair<Int, Int>
typealias Grid<T> = List<List<T>>
typealias GridPos = Vec2


var VERBOSE = false


fun <T> Grid<T>.width(): Int {
    return this.size
}

fun <T> Grid<T>.height(): Int {
    return this[0].size
}

fun <T> Grid<T>.bounds(): Vec2 {
    return Vec2(this.width() - 1, this.height() - 1)
}

fun <T> Grid<T>.includesPos(pos: GridPos): Boolean = pos inBounds bounds()


fun <T> Grid<T>.column(col: Int): List<T> = map { line -> line[col] }

fun <T> Grid<T>.positions(): List<GridPos> =
    flatMapIndexed { row, rowValues -> rowValues.indices.map { col -> row to col } }

fun <T> Grid<T>.getByPos(pos: GridPos): T = this[pos.first][pos.second]

fun <T, E> Grid<T>.mapGrid(map: (GridPos) -> E): Grid<E> =
    mapIndexed { row, rowValues -> rowValues.indices.map { map.invoke(row to it) } }

fun <T, E> Grid<T>.mapGridIndexed(mapIndexed: (GridPos, T) -> E): Grid<E> =
    mapIndexed { row, rowValues -> rowValues.mapIndexed { col, value -> mapIndexed.invoke(row to col, value) } }

fun <T> Grid<T>.copyWithValueSet(value: T, pos: GridPos): Grid<T> = mapIndexed { row, rowValues ->
    if (row == pos.first)
        rowValues.mapIndexed { col, colValue -> if (col == pos.second) value else colValue }
    else
        rowValues
}

fun <T> Grid<T>.withValues(valueMap: Map<GridPos, T>) =
    mapGrid { valueMap[it] ?: getByPos(it) }


enum class Direction(val vec2: Vec2) {
    UP(-1 to 0),
    DOWN(1 to 0),
    LEFT(0 to -1),
    RIGHT(0 to 1);

    fun right() = when (this) {
        UP -> RIGHT
        RIGHT -> DOWN
        DOWN -> LEFT
        LEFT -> UP
    }

    @Override
    override fun toString() =
        when (this) {
            UP -> "^"
            DOWN -> "v"
            LEFT -> "<"
            RIGHT -> ">"
        }

    fun left() = when (this) {
        UP -> LEFT
        LEFT -> DOWN
        DOWN -> RIGHT
        RIGHT -> UP
    }

    companion object {
        fun parse(char: Char) =
            when (char) {
                '^' -> UP
                'v' -> DOWN
                '<' -> LEFT
                '>' -> RIGHT
                else -> throw IllegalArgumentException("Unknown direction: $char")
            }

    }
}

enum class DiagonalDirection(val vec2: Vec2) {
    UP_RIGHT(-1 to 1),
    DOWN_RIGHT(1 to 1),
    DOWN_LEFT(1 to -1),
    UP_LEFT(-1 to 1);
}

infix fun Vec2.inBounds(bounds: Vec2): Boolean {
    return this.first in 0..bounds.first && this.second in 0..bounds.second
}


fun Vec2.move(direction: Direction): Vec2 {
    return this.plus(direction.vec2)
}

fun Vec2.move(direction: DiagonalDirection): Vec2 {
    return this.plus(direction.vec2)
}

operator fun Vec2.plus(vec2: Vec2): Vec2 {
    return Vec2(this.first + vec2.first, this.second + vec2.second)
}

operator fun Vec2.minus(vec2: Vec2): Vec2 {
    return Vec2(this.first - vec2.first, this.second - vec2.second)
}

operator fun Vec2.rem(vec2: Vec2): Vec2 {
    return Vec2(this.first % vec2.first, this.second % vec2.second)
}

operator fun Vec2.div(vec2: Vec2): Vec2 {
    return Vec2(this.first / vec2.first, this.second / vec2.second)
}

operator fun Vec2.div(scalar: Int): Vec2 {
    return Vec2(this.first / scalar, this.second / scalar)
}

operator fun Int.times(vec2: Vec2): Vec2 {
    return Vec2(this * vec2.first, this * vec2.second)
}

fun Vec2.dot(vec2: Vec2): Int {
    return this.first * vec2.first + this.second * vec2.second
}

fun Vec2.rot90(): Vec2 {
    return Vec2(this.second, -1 * this.first)
}

private fun pointsInUpperRightDiamondQuadrantShell(radius: Int): Sequence<Vec2> =
    (1..radius).asSequence()
        .map { x -> x to (radius - x) }

fun pointsInDiamondShell(radius: Int) =
    generateSequence(pointsInUpperRightDiamondQuadrantShell(radius).toList()) { rotatedPoints -> rotatedPoints.map { it.rot90() }}
        .take(4)
        .flatten()

fun pointsInDiamond(radius: Int) =
    (1..radius).asSequence()
        .flatMap { pointsInDiamondShell(it) }

fun <T, R> Iterable<T>.mapAsync(mapper: (T) -> R): Iterable<R> = runBlocking {
    map { async(Dispatchers.Default) { mapper.invoke(it) } }
        .awaitAll()
}

fun <T> Sequence<T>.split(predicate: (T) -> Boolean): Sequence<List<T>> =
    iterator().let { iterator ->
        sequence {
            while (iterator.hasNext()) {
                yield(sequence {
                    while (iterator.hasNext()) {
                        val element = iterator.next()
                        if (predicate(element)) {
                            break
                        } else {
                            yield(element)
                        }
                    }
                }.toList())
            }
        }
    }

data class ShortestPathResult<T>(
    val targets: List<T>,
    val distances: Map<T, Int>,
    val previous: Map<T, List<T>>
) {
    fun hasFoundTarget() = targets.isNotEmpty()

    fun minTargetDistance() = if (hasFoundTarget())
        targets.minOf { distances[it]!! }
    else
        -1

    private fun nearestTargets() =
        minTargetDistance()
            .let { minDist -> targets.filter { distances[it] == minDist }.toList() }


    private fun countShortestPathsTo(
        initialNode: T
    ): Map<T, Long> {
        val nodeQueue = PriorityQueue<Pair<T, T>>(compareBy { -distances[it.second]!! })
        nodeQueue.addAll(previous[initialNode]!!.map { initialNode to it })
        // Count the number of paths which lead to the initial node
        val pathCounts = mutableMapOf(initialNode to 1L)

        while (nodeQueue.isNotEmpty()) {
            val (parentNode, node) = nodeQueue.poll()
            if (node !in pathCounts) {
                // Continue search if this is the first visit of the node
                nodeQueue.addAll(previous[node]?.map { node to it } ?: emptyList())
            }
            pathCounts[node] = (pathCounts[node] ?: 0) + pathCounts[parentNode]!!
        }

        return pathCounts
    }


    fun nodesOfShortestPathToTarget() = if (hasFoundTarget())
        generateSequence(nearestTargets().first()) { previous[it]?.first() }.toSet()
    else
        emptySet()

    fun nodesOfAllShortestPathsToTarget() = if (hasFoundTarget())
        nearestTargets()
            .flatMap { countShortestPathsTo(it).keys }
            .toSet()
    else
        emptySet()

    fun countPathsToTarget(): Long = if (hasFoundTarget())
        nearestTargets()
            .flatMap { countShortestPathsTo(it).values }
            .max()
    else
        0


}

/**
 * https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
 */
fun <T> shortestPath(
    initialNode: T,
    neighbors: (T) -> Sequence<Pair<Int, T>>,
    isTarget: (T) -> Boolean,
): ShortestPathResult<T> {
    val nodeQueue = PriorityQueue<Pair<Int, T>>(compareBy { it.first })
    nodeQueue.add(0 to initialNode)

    val prev: MutableMap<T, MutableList<T>> = mutableMapOf()
    val dist: MutableMap<T, Int> = mutableMapOf(initialNode to 0)
    val targets: MutableList<T> = mutableListOf()

    while (nodeQueue.isNotEmpty()) {
        val (distance, node) = nodeQueue.poll()
        if (distance == dist[node]) {
            neighbors(node).forEach { (weight, neighbor) ->
                val bestDistance = dist[neighbor] ?: Int.MAX_VALUE
                val currentDistance = weight + distance

                if (currentDistance < bestDistance) {
                    prev[neighbor] = mutableListOf(node)
                    dist[neighbor] = currentDistance
                    if (isTarget(neighbor)) {
                        targets.add(neighbor)
                    } else {
                        nodeQueue.add(currentDistance to neighbor)
                    }
                } else if (currentDistance == bestDistance) {
                    prev[neighbor]!!.add(node)
                }

            }
        }
    }
    return ShortestPathResult(
        targets, dist, prev
    )
}