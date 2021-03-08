package com.example.testapp.data.db.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem (
        var item: ProductVariantWithImage,
        var quantity: Int,
        var price: Int,
        var totalPriceItem: Int
):Parcelable

@Parcelize
data class CartItems (
        var cartItems: MutableList<CartItem>
):Parcelable