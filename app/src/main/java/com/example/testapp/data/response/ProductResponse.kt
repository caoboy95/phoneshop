package com.example.testapp.data.response

import com.example.testapp.data.db.entities.Product

data class ProductResponse(
    val products: List<Product>
)