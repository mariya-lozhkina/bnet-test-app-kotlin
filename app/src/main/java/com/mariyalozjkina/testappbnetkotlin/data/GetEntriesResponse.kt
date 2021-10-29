package com.mariyalozjkina.testappbnetkotlin.data

import java.util.*

data class GetEntriesResponse(
    val status: Int,
    val data: ArrayList<ArrayList<Entry>>?
)
