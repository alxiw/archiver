package io.github.alxiw.archiver.controller

import io.github.alxiw.archiver.ArchiveException
import io.github.alxiw.archiver.core.ArchiverCore
import io.github.alxiw.archiver.model.Command

class ArchiverController(
    private val archiverCore: ArchiverCore
) {

    @Throws(ArchiveException::class)
    fun execute(command: Command) {
        val zipPath = command.zip
        if (zipPath == null || !zipPath.endsWith(".zip")) {
            throw ArchiveException()
        }

        perform(command, zipPath)
    }

    @Throws(ArchiveException::class)
    private fun perform(command: Command, zipPath: String) {
        when (command.name) {
            "p" -> {
                command.sources?.let { sources ->
                    archiverCore.pack(path = zipPath, sources = sources, comment = command.comment)
                }
            }
            "a" -> {
                command.sources?.let { sources ->
                    archiverCore.add(path = zipPath, sources = sources)
                }
                command.comment?.let { comment ->
                    archiverCore.setComment(path = zipPath, comment = comment)
                }
            }
            "e" -> {
                archiverCore.extract(path = zipPath, out = command.out)
            }
            "g" -> {
                val comment = archiverCore.getComment(zipPath) ?: "<empty>"
                println("comment: $comment")
            }
            else -> throw ArchiveException()
        }
    }
}
