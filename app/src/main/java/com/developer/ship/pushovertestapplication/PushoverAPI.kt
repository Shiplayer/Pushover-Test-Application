package com.developer.ship.pushovertestapplication

import com.developer.ship.pushovertestapplication.entity.PushoverResponse
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
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
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

            retrofit.create(PushoverAPI::class.java)
        }
    }

    @FormUrlEncoded
    @POST("1/messages.json")
    fun sendMessage(
            @Field("token") token:String = tokenApi,
            @Field("user") userToken:String,
            @Field("message") message:String
    ) : Single<PushoverResponse>
}