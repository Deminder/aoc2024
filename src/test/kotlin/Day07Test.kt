import com.github.deminder.Day07
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Day07Test {
    private val day = Day07

    private val inputString = """
190: 10 19
3267: 81 40 27
83: 17 5
156: 15 6
7290: 6 8 6 15
161011: 16 10 13
192: 17 8 14
21037: 9 7 18 13
292: 11 6 16 20
    """.trimIndent();

    @Test
    fun `test solve part 1`() {
        val result = day.solve(inputString.lineSequence(), false)

        assertEquals("3749", result)
    }

    @Test
    fun `test solve part 2`() {
        val result = day.solve(inputString.lineSequence(), true)

        assertEquals("11387", result)
    }
}