import com.github.deminder.Day04
import com.github.deminder.Day08
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Day04Test {

    private val day = Day04

    @Test
    fun `test solve part 1`() {
        val result = day.solve(
            """
        ....XXMAS.
        .SAMXMS...
        ...S..A...
        ..A.A.MS.X
        XMASAMX.MM
        X.....XA.A
        S.S.S.S.SS
        .A.A.A.A.A
        ..M.M.M.MM
        .X.X.XMASX
    """.trimIndent().lineSequence(), false
        )

        assertEquals("18", result)
    }

    @Test
    fun `test solve part 2`() {
        val result = day.solve(
            """
            .M.S......
            ..A..MSMS.
            .M.S.MAA..
            ..A.ASMSM.
            .M.S.M....
            ..........
            S.S.S.S.S.
            .A.A.A.A..
            M.M.M.M.M.
            ..........
        """.trimIndent().lineSequence(), true
        )

        assertEquals("9", result)
    }
}