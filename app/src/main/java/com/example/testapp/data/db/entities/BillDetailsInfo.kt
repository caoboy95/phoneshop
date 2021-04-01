package com.example.testapp.data.db.entities

data class BillDetailsInfo(
    val image: Image,
    val name: String,
    val price: Long,
    val productVariant: ProductVariant,
    val quantity: Int
)