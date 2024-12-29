import com.github.deminder.Day21
import com.github.deminder.Day21.moveToDirectionalInputCandidates
import com.github.deminder.shared.VERBOSE
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class Day21Test {

    private val day = Day21

    private val inputString = """
        029A
        980A
        179A
        456A
        379A
    """.trimIndent()


    @Test
    fun `test solve part 1`() {
        val result = day.solve(inputString.lineSequence(), false)

        assertEquals("126384", result)
    }


    @Test
    fun `test directional keyboard move`() {
        val result = day.directionalKeypad.moveToDirectionalInputCandidates(day.A, day.LEFT)

        val actual = result.joinToString(",") { it }
        assertEquals("<v<,v<<", actual)
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun before() {
            VERBOSE = true
        }
    }

}