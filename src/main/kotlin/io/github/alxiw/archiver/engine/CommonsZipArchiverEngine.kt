package io.github.alxiw.archiver.engine

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.apache.commons.compress.archivers.zip.ZipFile
import java.io.File
import kotlin.math.min

class CommonsZipArchiverEngine : ArchiverEngine {

    private var bufferSize = 4096

    override fun pack(path: String, sources: Array<String>, comment: String?): Int {
        var errorCount = 0

        ZipArchiveOutputStream(File(path)).use { zos ->
            comment?.let { zos.setComment(it) }
            sources.forEach { source ->
                errorCount += watchDir(zos, source, "")
            }
        }

        return errorCount
    }

    override fun add(path: String, sources: Array<String>): Int {
        var errorCount = 0

        val originalFile = File(path)
        val tempFile = File("${path}_temp")

        if (!originalFile.exists()) return 1
        originalFile.renameTo(tempFile)

        ZipArchiveOutputStream(originalFile).use { zos ->
            rewriteZip(tempFile, zos)
            sources.forEach { source ->
                errorCount += watchDir(zos, source, "")
            }
        }

        tempFile.delete()
        return errorCount
    }

    override fun setComment(path: String, comment: String): Int {
        val originalFile = File(path)
        val tempFile = File("${path}_temp")

        if (!originalFile.exists()) return 1
        originalFile.renameTo(tempFile)

        ZipArchiveOutputStream(originalFile).use { zos ->
            rewriteZip(tempFile, zos)
            zos.setComment(comment)
        }

        tempFile.delete()
        return 1
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
                            input.copyTo(output, bufferSize)
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

    private fun watchDir(zos: ZipArchiveOutputStream, path: String?, prefix: String): Int {
        if (path == null) return 1
        val file = File(path)
        if (!file.exists()) return 1

        val entryName = if (prefix.isNotEmpty()) "$prefix/${file.name}" else file.name

        return when {
            file.isDirectory -> {
                val listFiles = file.listFiles() ?: return 1
                var errors = 0
                listFiles.forEach { child ->
                    errors += watchDir(zos, child.path, entryName)
                }
                errors
            }
            file.isFile -> {
                archiveFile(zos, file, entryName)
                0
            }
            else -> 1
        }
    }

    private fun archiveFile(zos: ZipArchiveOutputStream, file: File, entryName: String) {
        val entry = ZipArchiveEntry(file, entryName)
        zos.putArchiveEntry(entry)
        file.inputStream().use { input ->
            input.copyTo(zos, bufferSize)
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
                    input.copyTo(zos, bufferSize)
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
