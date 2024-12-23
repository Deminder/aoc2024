package com.github.deminder

import com.github.deminder.shared.VERBOSE

object Day17 : Day {

    enum class Operation {
        ADV,
        BXL,
        BST,
        JNZ,
        BXC,
        OUT,
        BDV,
        CDV
    }

    data class QuineCandidate(
        val register: Long = 0,
        val shift: Int = 0,
    ) {

        /**
         * Return next candidates with a prepended 3-bit number
         */
        fun next(bits: Int = 3) =
            (0..<(2 shl bits))
                .asSequence()
                .map { it.toLong() shl shift }
                .map { it or register }
                .map { QuineCandidate(it, shift + bits) }


        fun outputWithA(computer: Computer) =
            computer.copy(a = register).output()
    }


    data class Computer(
        val program: List<Int>,
        val a: Long,
        val b: Long,
        val c: Long,
        val instructionPointer: Int = 0,
        val output: List<Int> = emptyList()
    ) {

        private fun operation() = Operation.entries[program[instructionPointer]]

        private fun operand() = program[instructionPointer + 1]

        private fun literal() = operand().toLong()

        private fun combo() = when (operand()) {
            0, 1, 2, 3 -> literal()
            4 -> a
            5 -> b
            6 -> c
            else -> throw IllegalArgumentException("Invalid operation")
        }

        private fun next() = copy(
            instructionPointer = instructionPointer + 2
        )

        private fun jump() = copy(
            instructionPointer = operand()
        )

        private fun divide(numerator: Long) = numerator shr combo().toInt()

        private fun mod8() = combo() % 8

        private fun tick() = operation().let { op ->
            when (op) {
                Operation.ADV -> copy(a = divide(a))
                Operation.BXL -> copy(b = b xor literal())
                Operation.BST -> copy(b = mod8())
                Operation.JNZ -> if (a == 0L) next() else jump()
                Operation.BXC -> copy(b = b xor c)
                Operation.OUT -> copy(output = output + mod8().toInt())
                Operation.BDV -> copy(b = divide(a))
                Operation.CDV -> copy(c = divide(a))
            }.let {
                when (op) {
                    Operation.JNZ -> it
                    else -> it.next()
                }
            }
        }

        private fun halted() = instructionPointer >= program.size

        // if (verbose) println("${it.operation()} ${it.operand()} \n A: ${it.a}\n B: ${it.b}\n C: ${it.c}");
        private fun run() =
            generateSequence(this) { it.tick() }
                .find { it.halted() }!!

        fun output() =
            run().output

        fun findQuineRegister() = program.indices
            .map { program.subList(0, it + 1) }
            .fold(
                QuineCandidate()
                    // Assume that the program number output only depends on the last 10 bits
                    .next(10)
                    .toSet()
            ) { frontier, expectedOutputPrefix ->
                if (VERBOSE)
                    println(frontier.size)
                frontier
                    .asSequence()
                    .filter { candidate ->
                        candidate.outputWithA(this).zip(expectedOutputPrefix)
                            .all { (a, b) -> a == b }
                    }
                    .flatMap { it.next(3) }
                    .toSet()
            }
            .find { it.outputWithA(this).size == program.size }!!
            .register
    }


    override fun solve(inputLines: Sequence<String>, part2: Boolean): String =
        parse(inputLines).let { computer ->
            if (part2)
                computer.findQuineRegister().toString()
            else
                computer.output().joinToString(",")
        }


    private fun parse(inputLines: Sequence<String>) = "\\d+".toRegex()
        .let { numberRegex -> inputLines.flatMap { numberRegex.findAll(it) } }
        .map { it.value.toLong() }
        .toList()
        .let { numbers ->
            Computer(
                numbers.subList(3, numbers.size).map { it.toInt() },
                numbers[0],
                numbers[1],
                numbers[2],
            )
        }


}


