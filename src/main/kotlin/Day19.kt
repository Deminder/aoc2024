package com.github.deminder

import com.github.deminder.shared.*

object Day19 : Day {


    data class TowelDesignRequest(
        val request: String,
        val towels: List<String>
    ) {

        private fun nextDesignOffsets(offset: Int) =
            towels.asSequence()
                .filter { request.substring(offset).startsWith(it) }
                .map { it.length to offset + it.length }


        fun countTowelDesignCombinations() = shortestPath(
            0,
            { nextDesignOffsets(it) },
            { it == request.length }
        ).countPathsToTarget()
    }


    override fun solve(inputLines: Sequence<String>, part2: Boolean): String =
        parse(inputLines)
            .let { towelDesignRequests ->
                towelDesignRequests
                    .map { it.countTowelDesignCombinations() }
                    .sumOf {
                        when {
                            part2 -> it
                            else -> if (it > 0) 1 else 0
                        }
                    }
            }
            .toString()


    private fun parse(inputLines: Sequence<String>) = inputLines.split { it.isEmpty() }
        .mapIndexed { index, lines ->
            if (index == 0) lines.first().split(", ") else lines.toList()
        }
        .toList()
        .let { (towels, towelRequests) -> towelRequests.map { TowelDesignRequest(it, towels) } }


}






