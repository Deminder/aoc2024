package com.github.deminder

import com.github.deminder.shared.*
import kotlin.math.absoluteValue

private typealias Key = Char
private typealias Keypad = Map<Key, Vec2>
private typealias KeySequence = String

object Day21 : Day {

    private const val GAP = '.'
    const val A = 'A'
    private const val UP = '^'
    const val LEFT = '<'
    private const val DOWN = 'v'
    private const val RIGHT = '>'

    private val numericKeypad: Keypad = mapOf(
        '7' to (0 to 0), '8' to (0 to 1), '9' to (0 to 2),
        '4' to (1 to 0), '5' to (1 to 1), '6' to (1 to 2),
        '1' to (2 to 0), '2' to (2 to 1), '3' to (2 to 2),
        GAP to (3 to 0), '0' to (3 to 1), A to (3 to 2),
    )

    val directionalKeypad: Map<Key, Vec2> = mapOf(
        GAP to (0 to 0), UP to (0 to 1), A to (0 to 2),
        LEFT to (1 to 0), DOWN to (1 to 1), RIGHT to (1 to 2),
    )

    private fun Keypad.keypadDiff(start: Key, end: Key) =
        this[end]!! - this[start]!!


    private fun moves(steps: Int, positiveKey: Key, negativeKey: Key) =
        (if (steps > 0) positiveKey else negativeKey)
            .let { step -> (1..steps.absoluteValue).map { step } }


    private fun Key.toDirection() = when (this) {
        LEFT -> Direction.LEFT
        DOWN -> Direction.DOWN
        RIGHT -> Direction.RIGHT
        UP -> Direction.UP
        else -> throw IllegalArgumentException("Invalid direction $this")
    }

    /**
     * Movement candidates to move from `start` to `end` without moving across GAP.
     */
    fun Keypad.moveToDirectionalInputCandidates(start: Key, end: Key) =
        keypadDiff(start, end)
            .let { (row, col) ->
                val gapPosition = this[GAP]!!
                val startPosition = this[start]!!

                moves(col, RIGHT, LEFT)
                    .plus(moves(row, DOWN, UP))
                    .permutations()
                    .toSet()
                    .filter { directions ->
                        directions.runningFold(startPosition) { pos, dir -> pos.move(dir.toDirection()) }
                            .all { it != gapPosition }
                    }
                    .map { it.joinToString("") }
            }


    fun KeySequence.toMoves(layer: Int) = "A${this}A"
        .asSequence()
        .windowed(2)
        .map { (start, end) -> KeyMove(layer, start, end) }
        .toList()


    data class KeyMove(
        val layer: Int,
        val start: Key,
        val end: Key
    ) {

        fun toDirectionalInput(keypad: Keypad) = keypad.moveToDirectionalInputCandidates(start, end)
            .map { it.toMoves(layer - 1) }

        override fun toString() = "[$layer] $start$end"
    }


    data class MinDirectionalInputLengthSearch(
        val numericKeyPadLayer: Int,
        val minKeyMoveLengths: Map<KeyMove, Long> = emptyMap()
    ) {

        private fun nextDirectionalInput(keyMove: KeyMove) =
            keyMove.toDirectionalInput(if (keyMove.layer == numericKeyPadLayer) numericKeypad else directionalKeypad)

        private fun sumLengthOfKeyMoves(keyMoves: List<KeyMove>) =
            keyMoves.sumOf { minKeyMoveLengths[it]!! }

        private fun aggregateMinMoveLengthForMove(keyMove: KeyMove) =
            copy(
                minKeyMoveLengths = minKeyMoveLengths.plus(
                    keyMove to nextDirectionalInput(keyMove)
                        .minOf { sumLengthOfKeyMoves(it) }
                )
            )

        private fun prepareMinMoveLengthsForMove(keyMove: KeyMove): MinDirectionalInputLengthSearch =
            nextDirectionalInput(keyMove)
                .let { candidates ->
                    if (keyMove.layer > 0)
                    // Consider directional keypads that robots are using
                        candidates.fold(this) { search, candidate ->
                            search.prepareLengthsOfKeyMoveSequence(candidate)
                        }
                    else
                    // Consider directional keypad that I am using
                        copy(
                            minKeyMoveLengths = minKeyMoveLengths.plus(
                                candidates.flatMap { candidate -> candidate.map { it to 1 } }
                            )
                        )
                }

        private fun prepareLengthsOfKeyMoveSequence(keySequenceMoves: List<KeyMove>) =
            keySequenceMoves.fold(this) { search, keyMove ->
                if (keyMove in minKeyMoveLengths)
                    search
                else
                    search
                        .prepareMinMoveLengthsForMove(keyMove)
                        .aggregateMinMoveLengthForMove(keyMove)
            }

        fun findMinLengthForNumericSequence(keySequence: KeySequence): Long =
            keySequence.toMoves(numericKeyPadLayer).let { moves ->
                prepareLengthsOfKeyMoveSequence(moves)
                    .sumLengthOfKeyMoves(moves)
            }
    }


    private fun minDirectionalInputLengthForNumericKeys(keys: KeySequence, layersUntilNumericKeypad: Int): Long =
        MinDirectionalInputLengthSearch(layersUntilNumericKeypad)
            .findMinLengthForNumericSequence(keys)


    private fun numericPart(keys: KeySequence) =
        keys
            .map { it.digitToInt() }
            .reduce { a, b -> (a * 10) + b }

    override fun solve(inputLines: Sequence<String>, part2: Boolean): String =
        parse(inputLines)
            .map { keys -> keys to minDirectionalInputLengthForNumericKeys(keys, if (part2) 25 else 2) }
            .sumOf { (keys, inputKeysCount) ->
                if (VERBOSE)
                    println("${keys}: $inputKeysCount")
                numericPart(keys) * inputKeysCount
            }
            .toString()


    private fun parse(inputLines: Sequence<String>): Sequence<KeySequence> =
        inputLines.map { line -> line.takeWhile { it.isDigit() } }


}


