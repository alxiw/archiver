package io.github.alxiw.archiver.model

data class Command(
    val name: String,
    val zip: String? = null,
    val sources: Array<String>? = null,
    val comment: String? = null,
    val out: String? = null
) {

    override fun toString(): String {
        return "Command(" +
                "name='$name', " +
                "zip='$zip', " +
                "sources='${sources?.joinToString(" ")}', " +
                "comment='$comment', " +
                "out='$out'" +
                ")"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Command

        if (name != other.name) return false
        if (zip != other.zip) return false
        if (!sources.contentEquals(other.sources)) return false
        if (comment != other.comment) return false
        if (out != other.out) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (zip?.hashCode() ?: 0)
        result = 31 * result + (sources?.contentHashCode() ?: 0)
        result = 31 * result + (comment?.hashCode() ?: 0)
        result = 31 * result + (out?.hashCode() ?: 0)
        return result
    }
}
