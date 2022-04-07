package com.example.weather.network

import com.example.weather.pojo.ReverseGeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface HereReverseGeocodingApiRetrofitInterface {
    @GET("revgeocode")
    suspend fun getAddressFromHereApi(
        @Query("at") location: String,
        @Query("lang") lang: String,
        @Query("apiKey") key: String
    ): ReverseGeocodingResponse
}