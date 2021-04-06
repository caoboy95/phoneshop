package com.example.testapp.ui.bill.viewmodel

import com.example.testapp.data.repository.BillRepository
import com.example.testapp.ui.base.BaseViewModel

class BillViewModel(
    private val repository: BillRepository
) : BaseViewModel(repository) {
    fun getCustomer(phone: String) = repository.getCustomer(phone)
}