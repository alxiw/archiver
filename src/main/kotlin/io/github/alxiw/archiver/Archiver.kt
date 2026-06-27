package io.github.alxiw.archiver

import com.github.ajalt.clikt.core.subcommands
import io.github.alxiw.archiver.controller.ArchiverController
import io.github.alxiw.archiver.core.CommonsZipArchiverCore
import io.github.alxiw.archiver.model.AddCommand
import io.github.alxiw.archiver.model.ArchiverCommand
import io.github.alxiw.archiver.model.Command
import io.github.alxiw.archiver.model.ExtractCommand
import io.github.alxiw.archiver.model.GetCommentCommand
import io.github.alxiw.archiver.model.PackCommand

fun main(args: Array<String>) {
    val core = CommonsZipArchiverCore()
    val controller = ArchiverController(core)

    val executeController: (Command) -> Unit = { command ->
        try {
            println(command.toString())
            controller.execute(command)
            println("Done!")
        } catch (e: ArchiveException) {
            println("An error occurred during processing")
        }
    }

    val mainCommand = ArchiverCommand().subcommands(
        PackCommand(executeController),
        AddCommand(executeController),
        ExtractCommand(executeController),
        GetCommentCommand(executeController)
    )

    mainCommand.main(args)
}

class ArchiveException : Throwable()
