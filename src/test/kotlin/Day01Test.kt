import com.github.deminder.Day01
import com.github.deminder.Day08
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Day01Test {

    private val day = Day01

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
        val result = day.solve(exampleInput.lineSequence(), false)

        assertEquals("11", result)
    }

    @Test
    fun `test solve part 2`() {
        val result = day.solve(exampleInput.lineSequence(), true)

        assertEquals("31", result)
    }
}