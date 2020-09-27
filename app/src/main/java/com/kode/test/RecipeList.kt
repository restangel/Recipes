package com.kode.test

import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter

open class RecipeList(
    uuid: String,
    name: String,
    val images: Array<String>,
    val lastUpdated: Long,
    val description: String?,
    val instructions: String?,
    val difficulty: Int
) : RecipeBrief(uuid, name) {

    constructor(other: RecipeList) : this(
        other.uuid, other.name, other.images, other.lastUpdated,
        other.description, other.instructions, other.difficulty
    )

    constructor(
        recipeBrief: RecipeBrief, images: Array<String>, lastUpdated: Long, description: String?,
        instructions: String?, difficulty: Int
    ) : this(
        recipeBrief.uuid, recipeBrief.name, images, lastUpdated, description, instructions,
        difficulty
    )

    companion object {
        fun fromJSON(json: JSONObject): RecipeList {
            val jsonArray = json.getJSONArray("images")
            val images = Array<String>(jsonArray.length()) {
                jsonArray.getString(it)
            }
            return RecipeList(
                RecipeBrief.fromJSON(json),
                images,
                json.getLong("lastUpdated"),
                json.getStringOrNull("description"),
                json.getStringOrNull("instructions"),
                json.getInt("difficulty")
            )
        }

        private fun JSONObject.getStringOrNull(name: String): String? {
            return if (this.has(name)) this.getString(name) else null
        }

        val listConverter = Converter<ResponseBody, Array<RecipeList>> { value ->
            val array = JSONObject(value.string()).getJSONArray("recipes")
            val res = Array(array.length()) {
                fromJSON(array.getJSONObject(it))
            }
            res
        }
    }
}