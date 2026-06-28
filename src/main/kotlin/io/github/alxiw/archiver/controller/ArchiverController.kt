package io.github.alxiw.archiver.controller

import io.github.alxiw.archiver.core.ArchiverCore
import io.github.alxiw.archiver.model.Action

class ArchiverController(
    private val archiverCore: ArchiverCore
) {

    fun execute(action: Action): Result<String?> = runCatching {
        require(action.zip.endsWith(".zip")) {
            "The archive file must have a .zip extension."
        }
        perform(action)
    }

    private fun perform(action: Action): String? {
        return when (action) {
            is Action.Pack -> {
                archiverCore.pack(path = action.zip, sources = action.sources, comment = action.comment)
                null
            }
            is Action.Add -> {
                action.sources?.let { sources ->
                    archiverCore.add(path = action.zip, sources = sources)
                }
                action.comment?.let { comment ->
                    archiverCore.setComment(path = action.zip, comment = comment)
                }
                null
            }
            is Action.Extract -> {
                archiverCore.extract(path = action.zip, out = action.out)
                null
            }
            is Action.GetComment -> {
                archiverCore.getComment(action.zip)
            }
        }
    }
}
