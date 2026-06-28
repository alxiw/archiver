package io.github.alxiw.archiver.model

sealed class Action {

    abstract val zip: String

    data class Pack(
        override val zip: String,
        val sources: Array<String>,
        val comment: String? = null
    ) : Action() {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as Pack
            if (zip != other.zip) return false
            if (!sources.contentEquals(other.sources)) return false
            if (comment != other.comment) return false
            return true
        }

        override fun hashCode(): Int {
            var result = zip.hashCode()
            result = 31 * result + (sources.contentHashCode())
            result = 31 * result + (comment?.hashCode() ?: 0)
            return result
        }

        override fun toString(): String {
            return "Pack(zip='$zip', sources=${sources.contentToString()}, comment=$comment)"
        }
    }

    data class Add(
        override val zip: String,
        val sources: Array<String>? = null,
        val comment: String? = null
    ) : Action() {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as Add
            if (zip != other.zip) return false
            if (!sources.contentEquals(other.sources)) return false
            if (comment != other.comment) return false
            return true
        }

        override fun hashCode(): Int {
            var result = zip.hashCode()
            result = 31 * result + (sources?.contentHashCode() ?: 0)
            result = 31 * result + (comment?.hashCode() ?: 0)
            return result
        }

        override fun toString(): String {
            return "Add(zip='$zip', sources=${sources.contentToString()}, comment=$comment)"
        }
    }

    data class Extract(
        override val zip: String,
        val out: String? = null
    ) : Action() {

        override fun toString(): String {
            return "Extract(zip='$zip', out=$out)"
        }
    }

    data class GetComment(
        override val zip: String
    ) : Action() {

        override fun toString(): String {
            return "GetComment(zip='$zip')"
        }
    }
}
