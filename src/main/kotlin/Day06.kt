package com.github.deminder

import com.github.deminder.shared.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.*

object Day06 : Day {

    data class Guard(val pos: Vec2, val direction: Direction) {

        fun step(): Guard {
            return copy(pos = pos.move(direction))
        }

        fun turn(): Guard {
            return copy(direction = direction.clockwise())
        }

        private fun positiveLinePositions(guardPos: Int, obstructions: List<Boolean>) =
            guardPos until obstructions.subList(guardPos, obstructions.size)
                .indexOf(true).let {
                    if (it == -1) obstructions.size + 1 else guardPos + it
                }

        private fun negativeLinePositions(reversePos: Int, reverseObstructions: List<Boolean>) =
            positiveLinePositions(reverseObstructions.size - 1 - reversePos, reverseObstructions.reversed())
                .map { pos -> reverseObstructions.size - 1 - pos }

        private fun linePositions(
            positiveDirection: Direction,
            guardPos: Int,
            obstructions: List<Boolean>
        ) =
            if (direction == positiveDirection)
                positiveLinePositions(guardPos, obstructions)
            else
                negativeLinePositions(guardPos, obstructions)


        fun positionsUntilLeavingOrNextObstruction(obstructions: Grid<Boolean>): List<Vec2> {
            val (row, col) = pos
            return when (direction) {
                Direction.RIGHT, Direction.LEFT -> linePositions(
                    Direction.RIGHT,
                    col,
                    obstructions[row]
                ).map { moveCol -> row to moveCol }

                Direction.DOWN, Direction.UP -> linePositions(
                    Direction.DOWN,
                    row,
                    obstructions.column(col)
                ).map { moveRow -> moveRow to col }
            }
        }

    }

    fun List<Guard>.indicateDirection() = map { it.direction }
        .let {
            if (it.size > 1) "+" else {
                when (it[0]) {
                    Direction.UP, Direction.DOWN -> "|"
                    else -> "-"
                }
            }
        }

    data class Board(
        val guard: Guard,
        val obstructions: Grid<Boolean>,
        val visits: Set<Guard> = emptySet(),
    ) {

        fun distinctPositionsVisitedBeforeLeavingBoard(): Set<Vec2> {
            return stepUntilLeavingOrLooping()
                .visits
                .map { it.pos }
                .toSet()
        }

        fun obstructionPositionsWhichCauseLooping(): Set<Vec2> = runBlocking {
            distinctPositionsVisitedBeforeLeavingBoard()
                .minus(guard.pos)
                // Try out each obstruction candidate
                .map {
                    async(Dispatchers.Default) {
                        it to obstruct(it).stepUntilLeavingOrLooping().inLoop()
                    }
                }.awaitAll()
                .filter { (_, isLoop) -> isLoop }
                .map { (pos, _) -> pos }
                .toSet()
        }

        private fun obstruct(pos: Vec2): Board =
            this.copy(obstructions = obstructions.mapIndexed { row, line ->
                if (row == pos.first)
                    line.mapIndexed { col, obstructed -> if (col == pos.second) true else obstructed }
                else
                    line
            })


        private fun stepUntilLeavingOrLooping(): Board =
            generateSequence(this) { it.stepUntilLeavingOrNextObstruction() }
                .find {
                    if (VERBOSE) {
                        println(it); println()
                    }
                    !it.guardIsInBoard() || it.inLoop()
                }!!

        private fun guardIsInBoard(): Boolean {
            return guard.pos inBounds obstructions.bounds()
        }

        private fun inLoop(): Boolean {
            return guard in visits
        }

        private fun stepUntilLeavingOrNextObstruction(): Board =
            guard
                .positionsUntilLeavingOrNextObstruction(obstructions)
                .let { positions ->
                    this.copy(
                        guard = guard.copy(pos = positions.last()).turn(),
                        visits = visits.plus(positions.dropLast(1).map { guard.copy(pos = it) })
                    )
                }

        @Override
        override fun toString() = visits.groupBy { v -> v.pos }
            .let { visitsByPos ->
                println(visitsByPos.size)
                obstructions
                    .mapIndexed { row, line ->
                        line.mapIndexed { col, obstructed ->
                            Vec2(row, col).let { pos ->
                                if (guard.pos == pos) guard.direction.toString() else {
                                    if (obstructed) "#" else {
                                        visitsByPos[pos]?.indicateDirection() ?: "."
                                    }
                                }
                            }
                        }.joinToString("")
                    }.joinToString("\n")
            }

        companion object {
            fun parse(inputLines: Sequence<String>): Board {
                val lines = inputLines.toList()
                val guard = Guard(
                    lines
                        .flatMapIndexed { row, line -> line.mapIndexed { col, char -> Vec2(row, col) to char } }
                        .find { (_, char) -> char == '^' }!!.first,
                    Direction.UP
                )
                val obstructions = lines
                    .map { line -> line.map { char -> char == '#' } }
                    .toList()

                return Board(
                    guard,
                    obstructions
                )
            }
        }
    }


    override fun solve(inputLines: Sequence<String>, part2: Boolean): String {
        val board = Board.parse(inputLines)

        return if (part2) {
            board
                .obstructionPositionsWhichCauseLooping()
        } else {
            board
                .distinctPositionsVisitedBeforeLeavingBoard()
        }
            .size
            .toString()
    }

}