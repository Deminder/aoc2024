package com.github.deminder

import com.github.deminder.shared.*

object Day15 : Day {

    enum class Tile {
        WALL,
        BOX,
        AIR;

        override fun toString() =
            when (this) {
                WALL -> "#"
                BOX -> "O"
                AIR -> "."
            }
    }

    data class Warehouse(
        val grid: Grid<Tile>,
        val robotPosition: Vec2,
        val doubleWideBoxes: Boolean
    ) {


        private fun findBoxInDirection(pushPosition: Vec2, direction: Direction) =
            pushPosition.move(direction)
                .let {
                    if (doubleWideBoxes)
                        sequenceOf(it.move(Direction.LEFT), it)
                    else
                        sequenceOf(it)
                }
                .find { grid.getByPos(it) == Tile.BOX }
                ?.let { if (doubleWideBoxes) listOf(it, it.move(Direction.RIGHT)) else listOf(it) }
                ?: emptyList()

        // Null: blocked
        // Empty: Air
        // NonEmpty: Set of moved boxes
        private fun findBoxesInFrontOfRobot(direction: Direction) =
            generateSequence(setOf(robotPosition) to emptySet<Vec2>()) { (frontier, visited) ->
                if (frontier.any { grid.getByPos(it.move(direction)) == Tile.WALL })
                // Blocked by wall
                    null
                else
                    frontier.asSequence()
                        .flatMap { pushPosition -> findBoxInDirection(pushPosition, direction) }
                        .filter { it !in frontier }
                        .filter { it !in visited }
                        .toSet() to visited.plus(frontier)

            }
                .find { (frontier, _) -> frontier.isEmpty() }
                ?.let { (_, visited) -> visited.filter { grid.getByPos(it) == Tile.BOX } }


        fun moveRobot(direction: Direction) = findBoxesInFrontOfRobot(direction)
            ?.let { boxes ->
                copy(
                    grid = grid.withValues(boxes.associateWith { Tile.AIR })
                        .withValues(boxes.map { it.move(direction) }.associateWith { Tile.BOX }),
                    robotPosition = robotPosition.move(direction)
                )
            } ?: this

        fun sumOfGps() = grid.mapGridIndexed { pos, value ->
            if (value == Tile.BOX)
                100 * pos.first + pos.second
            else
                0
        }.sumOf { it.sum() }


        override fun toString() =
            grid.mapGridIndexed { pos, tile -> if (pos == robotPosition) "@" else tile.toString() }
                .joinToString("\n") { it.joinToString("") }
    }


    override fun solve(inputLines: Sequence<String>, part2: Boolean): String =
        parse(inputLines, part2)
            .let { (initialWarehouse, moves) ->

                moves.fold(initialWarehouse) { warehouse, move ->
                    warehouse.moveRobot(move).let {
                        if (VERBOSE) {
                            println("Move $move")
                            println(it)
                        }
                        it
                    }
                }
            }.sumOfGps()
            .toString()


    private fun parseWarehouse(lines: List<String>, doubleWideness: Boolean): Warehouse =
        Warehouse(
            grid = lines.map { line ->
                line.map { c ->
                    when (c) {
                        '#' -> Tile.WALL
                        'O' -> Tile.BOX
                        else -> Tile.AIR
                    }
                }.flatMap {
                    if (doubleWideness)
                        listOf(it, if (it == Tile.WALL) it else Tile.AIR)
                    else
                        listOf(it)
                }
            }.toList(),
            robotPosition = lines.flatMapIndexed { row, line ->
                line.mapIndexed { col, it -> it to (row to (if (doubleWideness) col * 2 else col)) }
            }.find { (char, _) -> char == '@' }!!.second,
            doubleWideBoxes = doubleWideness
        )

    private fun parseMoves(lines: List<String>): List<Direction> =
        lines.flatMap { line -> line.map { Direction.parse(it) } }

    private fun parse(inputLines: Sequence<String>, doubleWideness: Boolean) = inputLines.split { it.isEmpty() }
        .take(2)
        .map { it.toList() }
        .toList()
        .let { (wareHouseLines, moveLines) ->
            parseWarehouse(wareHouseLines, doubleWideness) to parseMoves(moveLines)
        }


}


