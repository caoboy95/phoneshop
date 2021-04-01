package com.example.testapp.data.repository

import com.example.testapp.Constant.NODE_COMPANIES
import com.example.testapp.Constant.NODE_TYPE_PRODUCTS
import com.example.testapp.data.network.ProductApi

class ProductInfoRepository(
    private val api: ProductApi
) :BaseRepository() {
    //api
    suspend fun getProductVariant(id_product: Int) = safeApiCall { api.getProductVariants(id_product) }

    suspend fun getType(id: Int) = safeApiCall { api.getType(id) }

    suspend fun getBrand(id: Int) = safeApiCall { api.getBrand(id) }

    fun getTypeFromFirebase(typeID: Int) =
            firebaseDatabase.getReference(NODE_TYPE_PRODUCTS).orderByChild("id").equalTo(typeID.toDouble())

    fun getBrandFromFirebase(brandID: Int) =
            firebaseDatabase.getReference(NODE_COMPANIES).orderByChild("id").equalTo(brandID.toDouble())
}