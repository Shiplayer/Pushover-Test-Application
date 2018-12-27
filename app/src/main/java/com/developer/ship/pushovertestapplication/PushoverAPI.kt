package com.developer.ship.pushovertestapplication

import com.developer.ship.pushovertestapplication.entity.PushoverResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Shiplayer on 23.12.18.
 */
interface PushoverAPI {
    companion object {
        val tokenApi = "a96cxd1qxafaztwhq2hwbagkf2hb94"
        val baseURL = "https://api.pushover.net/"
        public val instance by lazy{
            val retrofit = Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()

            retrofit.create(PushoverAPI::class.java)
        }
    }

    @FormUrlEncoded
    @POST("1/messages.json")
    fun sendMessage(
            @Field("token") token:String = tokenApi,
            @Field("user") userToken:String,
            @Field("message") message:String,
            @Field("title") title:String?
    ) : Deferred<PushoverResponse>
}