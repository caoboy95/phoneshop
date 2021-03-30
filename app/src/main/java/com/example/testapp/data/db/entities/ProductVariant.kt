package com.example.testapp.data.db.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity
data class ProductVariant(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    val id_product: Int = 0,
    val name: String = "",
    val color: String = "",
    val version: String = "",
    val unit_price: Int = 0,
    val id_image: Int = 0,
    val quantity: Int = 0,
    val last_modified_by_user: Int = 0,
    val created_at: String = "",
    val updated_at: String = "",
): Parcelable

@Parcelize
data class ProductVariantWithImage(
        val image:  Image,
        val productVariant: ProductVariant
): Parcelable
