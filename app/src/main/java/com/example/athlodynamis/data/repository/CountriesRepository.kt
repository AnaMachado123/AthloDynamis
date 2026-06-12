package com.example.athlodynamis.data.repository

import com.example.athlodynamis.data.remote.RetrofitCountriesInstance

class CountriesRepository {

    suspend fun getLanguageFlags(): Pair<String, String> {
        return try {
            val countries = RetrofitCountriesInstance.api.getCountries()

            val portugalFlag = countries
                .firstOrNull { it.name.common.equals("Portugal", ignoreCase = true) }
                ?.flags
                ?.png
                ?: "https://flagcdn.com/w80/pt.png"

            val englishFlag = countries
                .firstOrNull {
                    it.name.common.equals("United Kingdom", ignoreCase = true) ||
                            it.name.common.equals("United States", ignoreCase = true)
                }
                ?.flags
                ?.png
                ?: "https://flagcdn.com/w80/gb.png"

            portugalFlag to englishFlag
        } catch (e: Exception) {
            "https://flagcdn.com/w80/pt.png" to "https://flagcdn.com/w80/gb.png"
        }
    }
}