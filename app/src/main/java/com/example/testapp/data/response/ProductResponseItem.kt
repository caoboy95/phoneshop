package com.example.testapp.data.response

data class ProductResponseItem(
    val created_at: String,
    val description: String,
    val id: Int,
    val id_company: Int,
    val id_type: Int,
    val image: String,
    val last_modified_by_user: Int,
    val name: String,
    val new: Int,
    val promotion_price: Int,
    val unit_price: Int,
    val updated_at: String
)