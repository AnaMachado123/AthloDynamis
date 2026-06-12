package com.example.athlodynamis.data.remote.api

import com.example.athlodynamis.data.remote.dto.CountryDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CountriesApiService {

    @GET("v3.1/all")
    suspend fun getCountries(
        @Query("fields") fields: String = "name,flags"
    ): List<CountryDto>
}