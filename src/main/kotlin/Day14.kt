package com.github.deminder

import com.github.deminder.shared.*

object Day14 : Day {

    var ROOM_SIZE = Vec2(101, 103)

    data class Robot(
        val pos: Vec2,
        val vel: Vec2,
    ) {
        fun move(time: Int): Vec2 = (pos + (time * (vel + ROOM_SIZE))) % ROOM_SIZE
    }

    private fun List<Vec2>.showGrid(): String =
        groupBy { it }.let { grid ->
            (0..<ROOM_SIZE.first)
                .joinToString("\n") { row ->
                    (0..<ROOM_SIZE.second)
                        .joinToString("") { col ->
                            grid[row to col]?.size?.toString() ?: "."
                        }
                }
        }

    private fun neighborPoints(point: Vec2) =
        DiagonalDirection.entries.map { point.move(it) } + Direction.entries.map { point.move(it) }


    fun findContinuousLine(points: Set<Vec2>) =
        generateSequence(setOf(points.first()) to emptySet<Vec2>()) { (frontier, visited) ->
            val nextFrontier = frontier.asSequence()
                .flatMap { neighborPoints(it) }
                .filter { it in points }
                .filter { it !in visited }
                .filter { it !in frontier }
                .toSet()
            nextFrontier to visited.plus(frontier)
        }
            .find { (frontier, _) -> frontier.isEmpty() }!!.second


    private fun countLines(points: Set<Vec2>) =
        generateSequence(points) { it.minus(findContinuousLine(it)) }
            .takeWhile { it.isNotEmpty() }
            .count() - 1


    private fun List<Robot>.safetyFactor(time: Int) = map { it.move(time) }
        .filter { it.first != (ROOM_SIZE / 2).first && it.second != (ROOM_SIZE / 2).second }
        .groupBy { it / ((ROOM_SIZE / 2) + (1 to 1)) }
        .values
        .map { it.size }
        .reduce { a, b -> a * b }


    override fun solve(inputLines: Sequence<String>, part2: Boolean): String =
        parse(inputLines).toList().let { robots ->
            if (part2) {
                (0..(ROOM_SIZE.first * ROOM_SIZE.second))
                    .mapAsync {
                        it to countLines(robots.map { r -> r.move(it) }.toSet())
                        // it to robots.safetyFactor(it)
                    }
                    .minBy { (_, count) -> count }
                    .let { (time, _) ->
                        if (VERBOSE)
                            println(robots.map { it.move(time) }.showGrid())
                        time
                    }
            } else
                robots.safetyFactor(100)

        }.toString()


    private fun parse(inputLines: Sequence<String>): Sequence<Robot> =
        inputLines
            .map { line ->
                "-?\\d+".toRegex()
                    .findAll(line)
                    .map { it.value.toInt() }
                    .toList()
            }
            .map { numbers ->
                Robot(
                    pos = Vec2(numbers[0], numbers[1]),
                    vel = Vec2(numbers[2], numbers[3]),
                )
            }


}


