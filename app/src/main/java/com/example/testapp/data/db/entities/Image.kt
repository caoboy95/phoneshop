package com.example.testapp.data.db.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Image(
    @PrimaryKey
    val id: Int = 0,
    val id_product: Int = 0,
    val link: String = "",
    val created_at: String = "",
    val updated_at: String = ""
): Parcelable
