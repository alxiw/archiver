package io.github.alxiw.archiver.model

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required

class MainCommand : CliktCommand(
    name = "archiver",
    help = "Command-line archiver for working with ZIP files"
) {

    override fun run() = Unit
}

class PackCommand(
    private val onExecute: (Action) -> Unit
) : CliktCommand(name = "pack", help = "Pack files into an archive") {

    val zip by option("-z", "--zip", help = "Path to the ZIP archive to create").required()
    val sources by option("-s", "--source", help = "Source files/folders to archive").multiple(required = true)
    val comment by option("-c", "--comment", help = "Archive comment")

    override fun run() {
        onExecute(Action.Pack(zip = zip, sources = sources.toTypedArray(), comment = comment))
    }
}

class AddCommand(
    private val onExecute: (Action) -> Unit
) : CliktCommand(name = "add", help = "Add files or a comment to an existing archive") {

    val zip by option("-z", "--zip", help = "Path to the ZIP archive").required()
    val sources by option("-s", "--source", help = "Files/folders to add").multiple()
    val comment by option("-c", "--comment", help = "Add or update the comment")

    override fun run() {
        onExecute(Action.Add(zip = zip, sources = sources.toTypedArray(), comment = comment))
    }
}

class ExtractCommand(
    private val onExecute: (Action) -> Unit
) : CliktCommand(name = "extract", help = "Extract an archive") {

    val zip by option("-z", "--zip", help = "Path to the ZIP archive to extract").required()
    val out by option("-o", "--out", help = "Output directory (defaults to the archive name)")

    override fun run() {
        onExecute(Action.Extract(zip = zip, out = out))
    }
}

class GetCommentCommand(
    private val onExecute: (Action) -> Unit
) : CliktCommand(name = "comment", help = "Read the comment from an archive") {

    val zip by option("-z", "--zip", help = "Path to the ZIP archive").required()

    override fun run() {
        onExecute(Action.GetComment(zip = zip))
    }
}
