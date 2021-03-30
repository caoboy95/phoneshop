package com.example.testapp.data.db.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class AddressCustomer(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var name: String,
    var email: String,
    var selected: Boolean,
    var phone: String,
    var address: String,
    var gender: String,
): Parcelable