package com.example.testapp.data.db.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Bill(
    val created_at: String = "",
    val date_order: String = "",
    val id: Int = 0,
    val id_customer: Int = 0,
    val id_user: Int = 0,
    val last_modified_by_user: Int = 0,
    val note: String = "",
    val payment: String = "",
    val status: Int = 0,
    val total: Long = 0,
    val updated_at: String = ""
): Parcelable

data class BillAndQuantity(
    val bill: Bill,
    val quantity: Int
)

data class  BillsWithCustomer(
    val billAndQuantity: List<BillAndQuantity>,
    val customer: Customer
)