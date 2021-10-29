package com.mariyalozjkina.testappbnetkotlin.data

data class GetEntriesResponse(
    val status: Int,
    val data: List<List<Entry>>?
)
