import com.github.deminder.solveDay03
import com.github.deminder.solveDay04
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Day04Test {


    @Test
    fun `test solve part 1`() {
        val result = solveDay04(
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
        val result = solveDay04(
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