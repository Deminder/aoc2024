package com.github.deminder.shared

typealias Vec2 = Pair<Int, Int>
typealias Grid<T> = List<List<T>>
typealias GridPos = Pair<Int, Int>


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

fun <T> Grid<T>.column(col: Int): List<T> = map { line -> line[col] }

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

infix fun Vec2.inBounds(bounds: Vec2): Boolean {
    return this.first in 0..bounds.first && this.second in 0..bounds.second
}

fun Vec2.move(direction: Direction): Vec2 {
    return this.plus(direction.vec2)
}

operator fun Vec2.plus(vec2: Vec2): Vec2 {
    return Vec2(this.first + vec2.first, this.second + vec2.second)
}

operator fun Vec2.minus(vec2: Vec2): Vec2 {
    return Vec2(this.first - vec2.first, this.second - vec2.second)
}

operator fun Int.times(vec2: Vec2): Vec2 {
    return Vec2(this * vec2.first, this * vec2.second)
}