package com.example.gblesson4.model.dto

import com.google.gson.annotations.SerializedName

data class FactDTO(
    @SerializedName("feels_like")
    val feelsLike: Int,
    val icon: String,
    var temp: Int,
)