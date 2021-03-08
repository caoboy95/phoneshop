package com.example.testapp.data.db.entities

data class BillDetailsInfo(
    val image: Image,
    val name: String,
    val price: Int,
    val productVariant: ProductVariant,
    val quantity: Int
)