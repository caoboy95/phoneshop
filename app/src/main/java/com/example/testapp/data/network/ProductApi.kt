package com.example.testapp.data.network

import com.example.testapp.data.db.entities.*
import com.example.testapp.data.response.*
import retrofit2.http.*

interface ProductApi {
    @GET("products")
    suspend fun getProduct(): ProductResponse

    @GET("products/{id}")
    suspend fun getProductDetail(
            @Path(value="id") id: Int
    ): ProductDetailResponse

    @GET("product/variants/{id}")
    suspend fun getProductVariants(
        @Path(value="id") id: Int
    ): ProductVariantResponse


    @POST("checkout")
    @Headers("Content-Type: application/json;charset=utf-8")
    suspend fun postCheckOut(
        @Body cartAndAddress: CartAndAddress
    ): ResponseMessage

    @GET("bills/{phone}")
    suspend fun getBills(
        @Path(value="phone") phone: String
    ):BillResponse

    @GET("bills/detail/{id}")
    suspend fun getBillDetails(
        @Path(value="id") idBill: Int
    ):BillDetailResponse

    @GET("type/{id}")
    suspend fun getType(
            @Path(value="id") id: Int
    ):TypeProduct

    @GET("brand/{id}")
    suspend fun getBrand(
            @Path(value="id") id: Int
    ):Brand
}

