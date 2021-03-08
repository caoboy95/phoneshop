package com.example.testapp.data.db.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Product (
    var created_at: String?,
    var description: String,
    @PrimaryKey(autoGenerate = false)
    var id: Int,
    var id_company: Int,
    var id_type: Int,
    var image: String,
    var last_modified_by_user: Int,
    var name: String,
    @ColumnInfo(name = "mnew")
    var mnew: Int,
    var promotion_price: Int,
    var unit_price: Int,
    var updated_at: String?
): Parcelable