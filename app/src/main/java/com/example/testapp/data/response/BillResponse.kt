package com.example.testapp.data.response

import com.example.testapp.data.db.entities.BillsWithCustomer

data class BillResponse(
    val billsWithCustomer: BillsWithCustomer
)