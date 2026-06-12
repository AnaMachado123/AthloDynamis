package com.example.athlodynamis.data.remote

import com.example.athlodynamis.data.remote.api.CountriesApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitCountriesInstance {

    private const val BASE_URL = "https://restcountries.com/"

    val api: CountriesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CountriesApiService::class.java)
    }
}