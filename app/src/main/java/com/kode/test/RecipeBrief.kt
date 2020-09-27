package com.kode.test

import org.json.JSONObject

open class RecipeBrief(
    val uuid: String,
    val name: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RecipeBrief) return false
        if (uuid.contentEquals(other.uuid)) return false
        return true
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    companion object {
        fun fromJSON(json: JSONObject): RecipeBrief {
            return RecipeBrief(json.getString("uuid"), json.getString("name"))
        }
    }
}