package io.github.alxiw.archiver.model

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required

// 1. PACK (p)
class PackCommand(private val onExecute: (Command) -> Unit) : CliktCommand(name = "pack", help = "Pack files into an archive") {
    val zip by option("-z", "--zip", help = "Path to the ZIP archive to create").required()
    val sources by option("-s", "--source", help = "Source files/folders to archive").multiple(required = true)
    val comment by option("-c", "--comment", help = "Archive comment")

    override fun run() {
        onExecute(Command(name = "p", zip = zip, sources = sources.toTypedArray(), comment = comment))
    }
}

// 2. ADD (a)
class AddCommand(private val onExecute: (Command) -> Unit) : CliktCommand(name = "add", help = "Add files or a comment to an existing archive") {
    val zip by option("-z", "--zip", help = "Path to the ZIP archive").required()
    val sources by option("-s", "--source", help = "Files/folders to add").multiple()
    val comment by option("-c", "--comment", help = "Add or update the comment")

    override fun run() {
        onExecute(Command(name = "a", zip = zip, sources = sources.toTypedArray(), comment = comment))
    }
}

// 3. EXTRACT (e)
class ExtractCommand(private val onExecute: (Command) -> Unit) : CliktCommand(name = "extract", help = "Extract an archive") {
    val zip by option("-z", "--zip", help = "Path to the ZIP archive to extract").required()
    val out by option("-o", "--out", help = "Output directory (defaults to the archive name)")

    override fun run() {
        onExecute(Command(name = "e", zip = zip, out = out))
    }
}

// 4. GET_COMMENT (g)
class GetCommentCommand(private val onExecute: (Command) -> Unit) : CliktCommand(name = "comment", help = "Read the comment from an archive") {
    val zip by option("-z", "--zip", help = "Path to the ZIP archive").required()

    override fun run() {
        onExecute(Command(name = "g", zip = zip))
    }
}
