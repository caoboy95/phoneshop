package com.example.testapp.data.db.entities

data class BillDetail (
        val created_at: String = "",
        val id: Int = 0,
        val id_bill: Int = 0,
        val id_product_variant: Int = 0,
        val quantity: Int = 0,
        val unit_price: Long = 0,
        val updated_at: String = ""
)