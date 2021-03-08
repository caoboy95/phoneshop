package com.example.testapp.data.repository

import com.example.testapp.data.network.ProductApi

class CheckBillDetailRepository(
    val api: ProductApi
): BaseRepository() {
    suspend fun getBillDetails(idBill: Int) = safeApiCall { api.getBillDetails(idBill) }
}