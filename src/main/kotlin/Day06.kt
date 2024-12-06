package com.github.deminder

import com.github.deminder.shared.Direction
import com.github.deminder.shared.Vec2
import com.github.deminder.shared.move
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.*


private data class Guard(val pos: Vec2, val direction: Direction) {
    fun stepCandidates(): Sequence<Guard> {
        return generateSequence(this) { it.turn() }
            .map { it.step() }
    }

    fun step(): Guard {
        return copy(pos = pos.move(direction))
    }

    private fun turn(): Guard {
        return copy(direction = direction.clockwise())
    }
}

private data class Board(
    val guard: Guard,
    val obstructions: Set<Vec2>,
    val bounds: Vec2,
    val visits: Set<Guard> = emptySet(),
) {

    companion object {
        fun parse(inputLines: Sequence<String>): Board {
            val points = inputLines
                .flatMapIndexed { row, line -> line.mapIndexed { col, char -> Vec2(row, col) to char } }
                .toList()
            val guard = Guard(
                points
                    .find { (_, char) -> char == '^' }!!.first,
                Direction.UP
            )
            val obstructions = points
                .filter { (_, char) -> char == '#' }
                .map { (pos, _) -> pos }
                .toSet()
            val positions = points
                .map { (pos, _) -> pos }

            return Board(
                guard,
                obstructions,
                Vec2(positions.maxOf { (row, _) -> row }, positions.maxOf { (_, col) -> col })
            )
        }
    }

    fun distinctPositionsVisitedBeforeLeavingBoard(): Set<Vec2> {
        return stepUntilLeavingBoardOrLooping()
            .visits
            .map { it.pos }
            .toSet()
    }

    fun obstructionPositionsWhichCauseLooping(): Set<Vec2> {
        val obstructionCandidates = distinctPositionsVisitedBeforeLeavingBoard()
            .minus(guard.pos)

        return runBlocking {
            obstructionCandidates
                .map {
                    async(Dispatchers.Default) {
                        it to stepUntilLeavingBoardOrLooping(it).inLoop()
                    }
                }.awaitAll()
                .filter { (_, isLoop) -> isLoop }
                .map { (pos, _) -> pos }
                .toSet()
        }
    }


    private fun stepUntilLeavingBoardOrLooping(extraObstruction: Vec2? = null): Board {
        var curStep = step(extraObstruction)
        do {
            val nextBoard = curStep.step(extraObstruction)
            if (curStep.guardIsInBoard() && !curStep.inLoop()) {
                curStep = nextBoard
            } else {
                break
            }
        } while (true)
        return curStep
    }

    private fun guardIsInBoard(): Boolean {
        return inBounds(guard.pos)
    }

    private fun inBounds(pos: Vec2): Boolean {
        return pos.first in 0..bounds.first && pos.second in 0..bounds.second
    }

    private fun inLoop(): Boolean {
        return visits.contains(guard)
    }

    private fun step(extraObstruction: Vec2? = null): Board {
        val nextGuard = guard
            .stepCandidates()
            .find { !obstructions.contains(it.pos) && extraObstruction != it.pos }!!

        return this.copy(
            guard = nextGuard,
            visits = visits.plus(guard)
        )
    }
}

fun solveDay06(inputLines: Sequence<String>, part2: Boolean): String {
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