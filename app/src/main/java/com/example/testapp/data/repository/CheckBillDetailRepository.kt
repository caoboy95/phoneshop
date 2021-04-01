package com.example.testapp.data.repository

import com.example.testapp.Constant.NODE_BILL_DETAILS
import com.example.testapp.Constant.NODE_IMAGES
import com.example.testapp.Constant.NODE_PRODUCTS
import com.example.testapp.Constant.NODE_PRODUCT_VARIANTS
import com.example.testapp.data.db.entities.ProductVariant
import com.example.testapp.data.network.ProductApi

class CheckBillDetailRepository(
    val api: ProductApi
): BaseRepository() {
    suspend fun getBillDetails(idBill: Int) = safeApiCall { api.getBillDetails(idBill) }

    fun getBillDetailsFromFirebase(idBill: Int) = firebaseDatabase.getReference(NODE_BILL_DETAILS).orderByChild("id_bill").equalTo(idBill.toDouble())

    fun getProductVariantOfBill(productVariantID: Int) = firebaseDatabase.getReference(NODE_PRODUCT_VARIANTS).orderByChild("id").equalTo(productVariantID.toDouble())

    fun getImageOfVariant(imageID: Int) = firebaseDatabase.getReference(NODE_IMAGES).orderByChild("id").equalTo(imageID.toDouble())

    fun getProductFromFirebase(productID: Int) = firebaseDatabase.getReference(NODE_PRODUCTS).orderByChild("id").equalTo(productID.toDouble())
}