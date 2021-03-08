package com.example.testapp.data.db.entities

data class Customer(
    val address: String,
    val created_at: String,
    val email: String,
    val gender: String,
    val id: Int,
    val last_modified_by_user: Int?,
    val name: String,
    val note: String?,
    val phone_number: String,
    val updated_at: String
)