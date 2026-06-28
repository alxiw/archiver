package io.github.alxiw.archiver.engine

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.apache.commons.compress.archivers.zip.ZipFile
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.math.min

class CommonsZipArchiverEngine : ArchiverEngine {

    override fun pack(path: String, sources: Array<String>, comment: String?) {
        ZipArchiveOutputStream(File(path)).use { zos ->
            comment?.let { zos.setComment(it) }
            sources.forEach { source ->
                watchDir(zos, source, "")
            }
        }
    }

    override fun add(path: String, sources: Array<String>) {
        val originalFile = File(path)
        if (!originalFile.exists()) throw FileNotFoundException("Archive file not found: $path")

        val tempFile = File("${path}_temp")
        if (!originalFile.renameTo(tempFile)) {
            throw IOException("Failed to create temporary file for updating archive: ${tempFile.path}")
        }

        try {
            ZipArchiveOutputStream(originalFile).use { zos ->
                rewriteZip(tempFile, zos)
                sources.forEach { source ->
                    watchDir(zos, source, "")
                }
            }
        } finally {
            tempFile.delete()
        }
    }

    override fun setComment(path: String, comment: String) {
        val originalFile = File(path)
        if (!originalFile.exists()) throw FileNotFoundException("Archive file not found: $path")

        val tempFile = File("${path}_temp")
        if (!originalFile.renameTo(tempFile)) {
            throw IOException("Failed to create temporary file for updating archive: ${tempFile.path}")
        }

        try {
            ZipArchiveOutputStream(originalFile).use { zos ->
                rewriteZip(tempFile, zos)
                zos.setComment(comment)
            }
        } finally {
            tempFile.delete()
        }
    }

    override fun extract(path: String, out: String?) {
        val outPath = out ?: path.substringBeforeLast('.')
        File(outPath).mkdirs()

        ZipFile.builder().setFile(File(path)).get().use { zip ->
            val entries = zip.entries
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                val outFile = File(outPath, entry.name)

                if (entry.isDirectory) {
                    outFile.mkdirs()
                } else {
                    outFile.parentFile.mkdirs()
                    zip.getInputStream(entry).use { input ->
                        outFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }
        }
    }

    override fun getComment(path: String): String? {
        val file = File(path)
        if (!file.exists()) return null

        val fileLength = file.length().toInt()
        val buffer = ByteArray(min(fileLength, 16384))

        file.inputStream().use { fis ->
            fis.skip((fileLength - buffer.size).toLong())
            val length = fis.read(buffer)
            if (length > 0) {
                return getZipCommentFromBuffer(buffer, length)
            }
        }
        return null
    }

    private fun watchDir(zos: ZipArchiveOutputStream, path: String?, prefix: String) {
        if (path == null) throw IllegalArgumentException("Source path cannot be null")

        val file = File(path)
        if (!file.exists()) throw FileNotFoundException("Source file not found: $path")

        val entryName = if (prefix.isNotEmpty()) "$prefix/${file.name}" else file.name

        when {
            file.isDirectory -> {
                val listFiles = file.listFiles() ?: throw IOException("Failed to list files in directory: $path")
                listFiles.forEach { child ->
                    watchDir(zos, child.path, entryName)
                }
            }
            file.isFile -> {
                archiveFile(zos, file, entryName)
            }
            else -> throw IOException("Unsupported file type: $path")
        }
    }

    private fun archiveFile(zos: ZipArchiveOutputStream, file: File, entryName: String) {
        val entry = ZipArchiveEntry(file, entryName)
        zos.putArchiveEntry(entry)
        file.inputStream().use { input ->
            input.copyTo(zos)
        }
        zos.closeArchiveEntry()
    }

    private fun rewriteZip(tempFile: File, zos: ZipArchiveOutputStream) {
        ZipFile.builder().setFile(tempFile).get().use { sZip ->
            val entries = sZip.entries
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                zos.putArchiveEntry(ZipArchiveEntry(entry.name))
                sZip.getInputStream(entry).use { input ->
                    input.copyTo(zos)
                }
                zos.closeArchiveEntry()
            }
            getComment(tempFile.path)?.let { zos.setComment(it) }
        }
    }

    private fun getZipCommentFromBuffer(buffer: ByteArray, length: Int): String? {
        val magicDirEnd = byteArrayOf(0x50, 0x4b, 0x05, 0x06)
        val bufferLength = min(buffer.size, length)

        for (i in bufferLength - magicDirEnd.size - 22 downTo 0) {
            val isMagicStart = magicDirEnd.indices.all { k -> buffer[i + k] == magicDirEnd[k] }
            if (isMagicStart) {
                val commentLength = (buffer[i + 20].toInt() and 0xFF) or ((buffer[i + 21].toInt() and 0xFF) shl 8)
                val realLength = bufferLength - i - 22
                return String(buffer, i + 22, min(commentLength, realLength), Charsets.UTF_8)
            }
        }

        return null
    }
}
