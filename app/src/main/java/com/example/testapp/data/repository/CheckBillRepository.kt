package com.example.testapp.data.repository

import com.example.testapp.Constant.NODE_BILLS
import com.example.testapp.Constant.NODE_BILL_DETAILS
import com.example.testapp.Constant.NODE_CUSTOMERS
import com.example.testapp.Constant.NODE_PRODUCT_VARIANTS
import com.example.testapp.data.network.ProductApi

class CheckBillRepository(
    val api: ProductApi
): BaseRepository() {
    suspend fun getBills(phone: String) = safeApiCall { api.getBills(phone) }

    fun getBillsFromFirebase(customerID: Int) = firebaseDatabase.getReference(NODE_BILLS).orderByChild("id_customer").equalTo(customerID.toDouble())

    fun getCustomerFromFirebase(phone: String) = firebaseDatabase.getReference(NODE_CUSTOMERS).orderByChild("phone_number").equalTo(phone)

    fun getBillDetailsFromFirebase(billID: Int) = firebaseDatabase.getReference(NODE_BILL_DETAILS).orderByChild("id_bill").equalTo(billID.toDouble())

    fun getProductVariantsFromFirebase(productVariantID: Int) = firebaseDatabase.getReference(NODE_PRODUCT_VARIANTS).orderByChild("id").equalTo(productVariantID.toDouble())
}