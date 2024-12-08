package com.github.deminder

private typealias Operation = (Long, Long) -> Long

object Day07 : Day {

    data class Equation(
        val testValue: Long,
        val operants: List<Long>
    ) {
        companion object {
            fun parse(inputLine: String): Equation = inputLine.split(": ")
                .let { (testValue, operants) ->
                    Equation(testValue.toLong(), operants.split(" ").map { it.toLong() })
                }
        }

        private fun maxValue(operations: List<Operation>) = operants
            .reduce { acc, operant -> operations.maxOf { it.invoke(acc, operant) } }

        private fun minValue(operations: List<Operation>) = operants
            .reduce { acc, operant -> operations.minOf { it.invoke(acc, operant) } }


        private fun useOperator(operator: (Long, Long) -> Long) =
            copy(operants = listOf(operator.invoke(operants[0], operants[1])).plus(operants.subList(2, operants.size)))


        fun couldBeTrue(operations: List<Operation>): Boolean = if (operants.size == 1) {
            operants.first() == testValue
        } else {
            maxValue(operations) >= testValue
                    && minValue(operations) <= testValue
                    && operations.any { useOperator(it).couldBeTrue(operations) }
        }
    }


    override fun solve(inputLines: Sequence<String>, part2: Boolean): String {
        val operations = listOf<Operation>(
            { a, b -> a + b },
            { a, b -> a * b }
        ).let {
            if (part2) {
                it.plus { a, b -> (a.toString(10) + b.toString(10)).toLong() }
            } else {
                it
            }
        }

        return inputLines
            .filter { it.isNotBlank() }
            .map { Equation.parse(it) }
            .filter { it.couldBeTrue(operations) }
            .sumOf { it.testValue }
            .toString()
    }

}