package com.example.testapp.data.db.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Product (
    var created_at: String? = null,
    var description: String = "",
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0,
    var id_company: Int = 0,
    var id_type: Int = 0,
    var image: String = "",
    var last_modified_by_user: Int = 0,
    var name: String = "",
    @ColumnInfo(name = "mnew")
    var mnew: Int = 0,
    var promotion_price: Int = 0,
    var unit_price: Int = 0,
    var updated_at: String? = null
): Parcelable