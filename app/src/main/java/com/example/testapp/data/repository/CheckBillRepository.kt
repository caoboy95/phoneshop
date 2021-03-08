package com.example.testapp.data.repository

import com.example.testapp.data.network.ProductApi

class CheckBillRepository(
    val api: ProductApi
): BaseRepository() {
    suspend fun getBills(phone: String)= safeApiCall { api.getBills(phone) }
}