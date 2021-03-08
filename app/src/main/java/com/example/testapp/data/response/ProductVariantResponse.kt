package com.example.testapp.data.response

import com.example.testapp.data.db.entities.ProductVariantWithImage

data class ProductVariantResponse(
    val product_variants: List<ProductVariantWithImage>
)
