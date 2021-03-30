package com.example.testapp.data.db.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Bill(
    val created_at: String,
    val date_order: String,
    val id: Int,
    val id_customer: Int,
    val id_user: Int?,
    val last_modified_by_user: Int?,
    val note: String?,
    val payment: String,
    val status: Int,
    val total: Int,
    val updated_at: String
): Parcelable

data class BillAndQuantity(
    val bill: Bill,
    val quantity: Int
)

data class  BillsWithCustomer(
    val billAndQuantity: List<BillAndQuantity>,
    val customer: Customer
)