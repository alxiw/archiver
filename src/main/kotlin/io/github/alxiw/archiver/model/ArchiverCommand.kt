package io.github.alxiw.archiver.model

import com.github.ajalt.clikt.core.CliktCommand

class ArchiverCommand : CliktCommand(
    name = "archiver",
    help = "Command-line archiver for working with ZIP files"
) {

    override fun run() = Unit // The main command does nothing on its own, it only routes
}
