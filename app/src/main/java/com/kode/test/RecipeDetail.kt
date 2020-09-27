package com.kode.test

import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter


class RecipeDetail(recipeList: RecipeList, val similar: Array<RecipeBrief>) :
    RecipeList(recipeList) {

    companion object {
        private fun fromJSON(json: JSONObject): RecipeDetail {
            val jsonArray = json.getJSONArray("similar")
            val briefs = Array(jsonArray.length()) {
                RecipeBrief.fromJSON(jsonArray.getJSONObject(it))
            }
            return RecipeDetail(RecipeList.fromJSON(json), briefs)
        }

        val converter =
            Converter<ResponseBody, RecipeDetail> { value ->
                val json = JSONObject(value.string()).getJSONObject("recipe")
                fromJSON(json)
            }
    }
}

