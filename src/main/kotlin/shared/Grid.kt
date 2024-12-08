package com.github.deminder.shared

typealias Vec2 = Pair<Int, Int>

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