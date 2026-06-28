package io.github.alxiw.archiver

import com.github.ajalt.clikt.core.subcommands
import io.github.alxiw.archiver.controller.ArchiverController
import io.github.alxiw.archiver.core.CommonsZipArchiverCore
import io.github.alxiw.archiver.model.AddCommand
import io.github.alxiw.archiver.model.MainCommand
import io.github.alxiw.archiver.model.Action
import io.github.alxiw.archiver.model.ExtractCommand
import io.github.alxiw.archiver.model.GetCommentCommand
import io.github.alxiw.archiver.model.PackCommand

fun main(args: Array<String>) {
    val core = CommonsZipArchiverCore()
    val controller = ArchiverController(core)

    val executeController: (Action) -> Unit = { action ->
        println(action.toString())
        controller.execute(action)
            .onSuccess { result ->
                when (action) {
                    is Action.Add,
                    is Action.Pack,
                    is Action.Extract -> {
                        println("Success")
                    }
                    is Action.GetComment -> {
                        result?.let { println("Comment: $it") }
                            ?: run { println("Comment is empty") }
                    }
                }
            }
            .onFailure { e -> println("Error: ${e.message}") }
    }

    val mainCommand = MainCommand().subcommands(
        PackCommand(executeController),
        AddCommand(executeController),
        ExtractCommand(executeController),
        GetCommentCommand(executeController)
    )

    mainCommand.main(args)
}
