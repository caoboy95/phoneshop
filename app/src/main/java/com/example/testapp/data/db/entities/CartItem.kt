package com.example.testapp.data.db.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem (
        var item: ProductVariant,
        var quantity: Int,
        var price: Long,
        var totalPriceItem: Long
):Parcelable

@Parcelize
data class CartItems (
        var cartItems: MutableList<CartItem>
):Parcelable