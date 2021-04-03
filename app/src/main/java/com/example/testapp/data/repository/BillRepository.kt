package com.example.testapp.data.repository

import com.example.testapp.Constant

class BillRepository : BaseRepository() {
    fun getCustomer(phone: String) = firebaseDatabase.getReference(Constant.NODE_CUSTOMERS).orderByChild("phone_number").equalTo(phone)
}