package com.example.testapp.data.repository

import com.example.testapp.data.network.ProductApi

class ProductInfoRepository(
    private val api: ProductApi
) :BaseRepository() {
    suspend fun getProductVariant(id_product: Int) = safeApiCall { api.getProductVariants(id_product) }

    suspend fun getType(id: Int) = safeApiCall { api.getType(id) }

    suspend fun getBrand(id: Int) = safeApiCall { api.getBrand(id) }
}