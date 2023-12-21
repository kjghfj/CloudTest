package com.cloudangle.cloud_test

import com.google.gson.annotations.SerializedName

data class PlacesResponse(
    @SerializedName("results") val results: List<Place>,
    // Add other fields if needed
)

