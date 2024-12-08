import com.github.deminder.Day02
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Day02Test {

    private val day = Day02

    private val exampleInput = """
        7 6 4 2 1
        1 2 7 8 9
        9 7 6 2 1
        1 3 2 4 5
        8 6 4 4 1
        1 3 6 7 9
    """.trimIndent()

    @Test
    fun `test solve part 1`() {
        val result = day.solve(exampleInput.lineSequence(), false)

        assertEquals("2", result)
    }

    @Test
    fun `test solve part 2`() {
        val result = day.solve(exampleInput.lineSequence(), true)

        assertEquals("4", result)
    }
}