package com.mariyalozjkina.testappbnetkotlin

import java.util.ArrayList

data class GetEntriesResponse(
    val status: Int,
    val data: ArrayList<ArrayList<Entry>>?
)
