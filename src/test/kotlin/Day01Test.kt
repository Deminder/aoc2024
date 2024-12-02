import com.github.deminder.solveDay01
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class Day01Test {

    private val exampleInput = """
        3   4
        4   3
        2   5
        1   3
        3   9
        3   3
    """.trimIndent()

    @Test
    fun `test solve part 1`() {
        val result = solveDay01(exampleInput.lineSequence(), false)

        assertEquals("11", result)
    }

    @Test
    fun `test solve part 2`() {
        val result = solveDay01(exampleInput.lineSequence(), true)

        assertEquals("31", result)
    }
}