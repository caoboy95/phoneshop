package com.example.testapp.data.db.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity
data class ProductVariant(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val id_product: Int,
    val name: String,
    val color: String,
    val version: String,
    val unit_price: Float,
    val id_image: Int,
    val quantity: Int,
    val last_modified_by_user: Int,
    val created_at: String,
    val updated_at: String,
): Parcelable

@Parcelize
data class ProductVariantWithImage(
        val image:  Image,
        val productVariant: ProductVariant
): Parcelable
