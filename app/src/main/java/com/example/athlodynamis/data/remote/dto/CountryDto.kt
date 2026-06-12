package com.example.athlodynamis.data.remote.dto

data class CountryDto(
    val name: CountryNameDto,
    val flags: CountryFlagsDto
)

data class CountryNameDto(
    val common: String
)

data class CountryFlagsDto(
    val png: String
)