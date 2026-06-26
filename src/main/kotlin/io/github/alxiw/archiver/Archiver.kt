package io.github.alxiw.archiver

import io.github.alxiw.archiver.controller.ArchiverController
import io.github.alxiw.archiver.core.CommonsZipArchiverCore
import io.github.alxiw.archiver.parser.Actions
import io.github.alxiw.archiver.parser.Command
import io.github.alxiw.archiver.parser.CommandParser
import org.apache.commons.cli.ParseException

fun main(args: Array<String>) {
    if (args.isNotEmpty()) {
        val parser = CommandParser(args)
        val command: Command
        try {
            command = parser.parse()
            if (command.name == "h") {
                printHelpMessage()
            } else {
                println(command.toString())
                try {
                    val core = CommonsZipArchiverCore()
                    val controller = ArchiverController(core)
                    controller.execute(command)
                    println("Done!")
                } catch (e: ArchiveException) {
                    println("An error occurred during processing")
                }
            }
        } catch (e: ParseException) {
            println("Unavailable action. Run with '-help' for usage information.")
        }

    } else {
        printHelpMessage()
    }
}

private fun printHelpMessage() {
    println("Available actions in the command line")
    for (i in Actions.entries.toTypedArray().indices) {
        println("${i + 1}. ${Actions.entries[i].action}\n   ${Actions.entries[i].format}")
    }
}

class ArchiveException : Throwable()
