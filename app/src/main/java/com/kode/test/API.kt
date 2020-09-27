package com.kode.test

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import java.lang.reflect.Type

interface API {
    companion object {
        private const val baseUrl = "https://test.kode-t.ru/"

        private val factory = object : Converter.Factory() {
            override fun responseBodyConverter(
                type: Type,
                annotations: Array<Annotation>,
                retrofit: Retrofit
            ): Converter<ResponseBody, *>? {

                if(getRawType(type) == emptyArray<RecipeList>()::class.java)
                    return RecipeList.listConverter
                if(getRawType(type) == RecipeDetail::class.java)
                    return RecipeDetail.converter
                return null
            }
        }

        val instance = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(factory)
            .build()
            .create(API::class.java)
    }

    @GET("/recipes")
    suspend fun list(): Array<RecipeList>

    @GET("/recipes/{uuid}")
    suspend fun detailed(@Path("uuid") uuid: String): RecipeDetail
}