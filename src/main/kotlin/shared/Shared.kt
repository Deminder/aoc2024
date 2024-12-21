package com.github.deminder.shared

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

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

fun <T, E> Grid<T>.mapGridIndexed(mapIndexed: (T, GridPos) -> E): Grid<E> =
    mapIndexed { row, rowValues -> rowValues.mapIndexed { col, value -> mapIndexed.invoke(value, row to col) } }

fun <T> Grid<T>.copyWithValueSet(value: T, pos: GridPos): Grid<T> = mapIndexed { row, rowValues ->
    if (row == pos.first)
        rowValues.mapIndexed { col, colValue -> if (col == pos.second) value else colValue }
    else
        rowValues
}

enum class Direction(val vec2: Vec2) {
    UP(-1 to 0),
    DOWN(1 to 0),
    LEFT(0 to -1),
    RIGHT(0 to 1);

    fun clockwise(): Direction {
        return when (this) {
            UP -> RIGHT
            RIGHT -> DOWN
            DOWN -> LEFT
            LEFT -> UP
        }
    }

    @Override
    override fun toString() =
        when (this) {
            UP -> "^"
            DOWN -> "v"
            LEFT -> "<"
            RIGHT -> ">"
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

fun <T, R> Iterable<T>.mapAsync(mapper: (T) -> R): Iterable<R> = runBlocking {
    map { async(Dispatchers.Default) { mapper.invoke(it) } }
        .awaitAll()
}