package io.github.alxiw.archiver.engine

interface ArchiverEngine {

    fun pack(path: String, sources: Array<String>, comment: String?): Int
    fun add(path: String, sources: Array<String>): Int
    fun setComment(path: String, comment: String): Int
    fun extract(path: String, out: String?)
    fun getComment(path: String): String?
}
