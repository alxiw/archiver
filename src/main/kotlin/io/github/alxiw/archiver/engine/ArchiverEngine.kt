package io.github.alxiw.archiver.engine

interface ArchiverEngine {

    fun pack(path: String, sources: Array<String>, comment: String?)
    fun add(path: String, sources: Array<String>)
    fun setComment(path: String, comment: String)
    fun extract(path: String, out: String?)
    fun getComment(path: String): String?
}
