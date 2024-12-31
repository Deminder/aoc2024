package com.github.deminder


import com.github.deminder.shared.VERBOSE
import com.github.deminder.shared.pairs
import com.github.deminder.shared.shortestPath
import com.github.deminder.shared.split

object Day24 : Day {

    data class OperationSharedProperties(
        val xyDistance: Int,
        val bitOperation: String,
        val gap: Int
    ) {
        override fun toString() = "[$bitOperation] via $xyDistance skipping $gap"
    }

    data class OperationProperties(
        val outputLabel: String,
        val bitOperation: String,
        val xyDistance: Int,
        val xyIndex: Int,
        val zIndex: Int
    ) {
        fun shared() = OperationSharedProperties(
            xyDistance,
            bitOperation,
            gap()
        )

        private fun gap() = zIndex - xyIndex

        fun deriveExceptedZIndex(commonKeys: Map<Pair<String, Int>, OperationSharedProperties>) =
            zIndex + (commonKeys[bitOperation to xyDistance]
                ?.let { it.gap - gap() }
                ?: 0)

        override fun toString() = "[$bitOperation] $outputLabel via $xyDistance from $xyIndex to $zIndex"
    }

    data class BitOperation(
        val operantLabel1: String,
        val operantLabel2: String,
        val bitOperation: String
    ) {
        private fun applyOperation(a: Boolean, b: Boolean) = when (bitOperation) {
            "XOR" -> a xor b
            "AND" -> a and b
            else -> a or b
        }


        fun apply(values: Map<String, Boolean>) = applyOperation(values[operantLabel1]!!, values[operantLabel2]!!)

    }

    fun String.numberSuffix() = substring(1..<length).toInt()

    data class Network(
        val values: Map<String, Boolean>,
        val connections: Map<String, BitOperation>,
        val swaps: Map<String, String> = emptyMap()
    ) {

        fun shortestPathToInput(label: String) = shortestPath(
            label,
            { candidate ->
                operation(candidate)
                    .let { sequenceOf(it.operantLabel1, it.operantLabel2) }
                    .map { 1 to it }
            },
            { it.startsWith("x") || it.startsWith("y") }
        )

        private fun outputLabels(label: String) = connections.entries
            .asSequence()
            .filter { (_, op) -> label in listOf(op.operantLabel1, op.operantLabel2) }
            .map { (outputLabel, _) -> swaps[outputLabel] ?: outputLabel }

        fun shortestPathToOutput(label: String) = shortestPath(
            label,
            { candidate -> outputLabels(candidate).map { 1 to it } },
            { it.startsWith("z") }
        )

        fun characteristicConnectionMap(): Map<OperationSharedProperties, List<OperationProperties>> = connections.keys
            .map { outputLabel ->
                (shortestPathToInput(outputLabel) to shortestPathToOutput(outputLabel)).let { (input, output) ->
                    OperationProperties(
                        outputLabel,
                        operation(outputLabel).bitOperation,
                        input.minTargetDistance(),
                        input.targets.firstOrNull()?.numberSuffix() ?: -1,
                        output.targets.firstOrNull()?.numberSuffix() ?: -1
                    )
                }
            }
            .groupBy { it.shared() }


        private fun labels(name: String) = (values.keys + connections.keys)
            .filter { it.startsWith(name) }
            .sorted()

        fun labelDecimal(name: String) = labels(name)
            .map { values[it]!! }
            .foldRight(0L) { bit, acc -> (acc * 2) + (if (bit) 1 else 0) }

        fun swap(a: String, b: String) = copy(swaps = swaps.plus(sequenceOf(a to b, b to a)))

        private fun labelByIndex(name: String, index: Int) = name + index.toString().padStart(2, '0')

        private fun zeroInput() = copy(values = values.mapValues { false })

        private fun computeWithAdditionBits(
            index: Int, x: Boolean, y: Boolean
        ) =
            copy(
                values = values.plus(
                    sequenceOf(
                        labelByIndex("x", index) to x,
                        labelByIndex("y", index) to y,
                    )
                )
            ).computeLabels(
                listOf(
                    labelByIndex("z", index),
                    labelByIndex("z", index + 1)
                )
            )

        private fun checkBitOutputAtIndex(index: Int, bitOutputs: List<Boolean>) =
            bitOutputs == listOf(
                values[labelByIndex("z", index)]!!,
                values[labelByIndex("z", index + 1)]!!,
            )

        private fun checkBitAdditionOutput(index: Int) =
            sequenceOf(false, true)
                .flatMap { x ->
                    sequenceOf(false, true)
                        .map { x to it }
                        .map { (x, y) -> computeWithAdditionBits(index, x, y) to listOf(x xor y, x and y) }
                }
                .all { (network, bitOutputs) -> network.checkBitOutputAtIndex(index, bitOutputs) }

        fun countCorrectAdditionBits() = zeroInput().let { zero ->
            (0..<labels("x").size)
                .count { zero.checkBitAdditionOutput(it) }
        }

        fun computeZDecimal() = computeLabels(labels("z"))
            .labelDecimal("z")

        private fun computeLabels(labels: List<String>, origins: Set<String> = emptySet()): Network =
            labels.fold(this) { network, label ->
                if (VERBOSE) println(network.values); network.computeLabel(label, origins)
            }

        private fun computeLabel(label: String, origins: Set<String> = emptySet()) =
            computeLabelDependencies(label, origins)
                .computeLabelDirect(label)

        private fun operation(label: String) = connections[swaps[label] ?: label]!!

        private fun computeLabelDirect(label: String) = copy(
            values = values + (label to operation(label).apply(values))
        )

        private fun computeLabelDependencies(label: String, origins: Set<String> = emptySet()) =
            operation(label).let { op ->
                if (label in origins)
                // Resolve cycle by setting values to false
                    copy(values = values + (op.operantLabel1 to false) + (op.operantLabel2 to false))
                else
                // Resolve dependency
                    computeLabels(
                        listOf(op.operantLabel1, op.operantLabel2).filter { it !in values },
                        origins.plus(label)
                    )
            }

        fun useSwaps(swaps: Collection<Pair<String, String>>) =
            swaps.fold(this) { n, (a, b) -> n.swap(a, b) }

    }


    private fun findAllSetsOfFourDisjunctPairs(pairs: List<Pair<String, String>>) = generateSequence(
        pairs
            .map { setOf(it) }
            .toSet()
    ) { candidates ->
        candidates
            .flatMap { candidate ->
                candidate
                    .flatMap { it.toList() }
                    .toSet()
                    .let { visited ->
                        pairs
                            .filter { it.first !in visited && it.second !in visited }
                            .map { candidate + it }
                    }
            }
            .toSet()
    }
        .take(4)
        .last()

    override fun solve(inputLines: Sequence<String>, part2: Boolean): String =
        parse(inputLines)
            .let { network ->
                if (part2) {
                    val (uncommon, common) = network.characteristicConnectionMap().entries
                        .partition { (_, values) -> values.size <= 4 }
                    val commonKeys =
                        common.associate { (shared, _) -> (shared.bitOperation to shared.xyDistance) to shared }

                    if (VERBOSE)
                        uncommon.forEach { (k, v) -> println("$k : $v\n") }

                    val swapCandidates = uncommon.flatMap { (_, nodes) -> nodes }
                        .pairs()
                        .filter { (a, b) ->
                            a.zIndex == b.deriveExceptedZIndex(commonKeys) &&
                                    b.zIndex == a.deriveExceptedZIndex(commonKeys)
                        }
                        .map { (a, b) -> a.outputLabel to b.outputLabel }
                        .toList()

                    findAllSetsOfFourDisjunctPairs(swapCandidates)
                        .maxByOrNull { network.useSwaps(it).countCorrectAdditionBits() }
                        ?.flatMap { it.toList() }
                        ?.sorted()
                        ?.joinToString(",")
                        ?: "?"

                } else
                    network.computeZDecimal().toString()
            }


    fun parse(inputLines: Sequence<String>) = inputLines.split { it.isBlank() }
        .toList()
        .let { (initLines, wireLines) ->
            Network(
                initLines.map { line -> line.split(":") }
                    .associate { (label, bit) -> label to ("1" in bit) },
                wireLines.map { line -> line.split(" ") }
                    .associate { (a, o, b, _, c) -> c to BitOperation(a, b, o) }
            )

        }


}