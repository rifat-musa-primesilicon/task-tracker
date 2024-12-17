package com.r_mit.taskt

data class UpdateRequestBody(
    val values: List<List<String>> // Represents the 2D array of data to update
)
