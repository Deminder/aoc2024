import com.github.deminder.Day03
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Day03Test {

    private val day = Day03

    @Test
    fun `test solve part 1`() {
        val result = day.solve(
            "xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))".lineSequence(),
            false
        )

        assertEquals("161", result)
    }

    @Test
    fun `test solve part 2`() {
        val result = day.solve(
            "xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))".lineSequence(),
            true
        )

        assertEquals("48", result)
    }
}