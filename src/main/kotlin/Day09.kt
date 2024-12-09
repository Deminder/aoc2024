package com.github.deminder

import com.github.deminder.shared.VERBOSE
import kotlin.math.min

private typealias FileId = Int


object Day09 : Day {

    data class Gap(
        val offset: Long,
        val size: Long
    )

    data class File(
        val id: FileId,
        val size: Long,
        val offset: Long,
    ) {

        private fun sumTo(n: Long) = (n * (n - 1)) / 2

        fun checkSum() = id * (sumTo(offset + size) - sumTo(offset))

        fun copyFileTo(gap: Gap): File = copy(
            size = min(gap.size, size),
            offset = gap.offset
        )

        fun subtractCopiedSize(otherFile: File) = copy(
            size = size - otherFile.size,
        )

        fun gap(otherFile: File): Gap = (this.offset + this.size)
            .let { start -> Gap(start, (otherFile.offset - start)) }
    }


    private fun List<File>.findMoveTargetOnLeft(moveFile: File, completeFile: Boolean) = windowed(2)
        .map { (a, b) -> moveFile.copyFileTo(a.gap(b)) }
        .find { if (completeFile) it.size == moveFile.size else it.size > 0 }

    private fun List<File>.moveSorted(moveTarget: File, rightFile: File) =
        indexOfFirst { it.offset > moveTarget.offset }
            .let { insertIndex -> subList(0, insertIndex).plus(moveTarget).plus(this.subList(insertIndex, this.size)) }
            .let { withMoveTarget ->
                rightFile.subtractCopiedSize(moveTarget)
                    .let { reducedLast ->
                        if (reducedLast.size > 0)
                        // Partial move: Reintroduce residual size as right file
                            withMoveTarget.map { if (it.offset == rightFile.offset) reducedLast else it } to true
                        else
                        // Complete move: Done
                            withMoveTarget.filter { it.offset != rightFile.offset } to false
                    }
            }

    private fun List<File>.moveFileFromRightToLeft(rightFile: File, completeFile: Boolean): List<File>? =
        findMoveTargetOnLeft(rightFile, completeFile)
            ?.let { leftFromRightFile ->
                if (leftFromRightFile.offset < rightFile.offset) {
                    moveSorted(leftFromRightFile, rightFile).let { (nextFiles, isPartialMove) ->
                        if (isPartialMove)
                        // Partial move: Continue to move residual
                            nextFiles.moveFileFromRightToLeft(nextFiles.last(), completeFile) ?: nextFiles
                        else
                        // Complete move: Done
                            nextFiles
                    }
                } else null
            }

    private fun List<File>.moveFilesFromRightToLeft(completeFiles: Boolean) = foldRight(this) { rightFile, files ->
        files.moveFileFromRightToLeft(rightFile, completeFiles) ?: files
    }

    override fun solve(inputLines: Sequence<String>, part2: Boolean): String {
        val numbers = inputLines.last().map { it - '0' }.plus(0)
        val files: List<File> = numbers
            .windowed(2, 2)
            .runningFoldIndexed(0 to File(0, 0, 0)) { fileId, (prevGapSize, prevFile), (fileSize, gapSize) ->
                gapSize to File(fileId, fileSize.toLong(), prevFile.offset + prevFile.size + prevGapSize)
            }
            .drop(1)
            .map { (_, file) -> file }

        return files.moveFilesFromRightToLeft(part2)
            .sumOf { if (VERBOSE) println(it); it.checkSum() }
            .toString()
    }
}