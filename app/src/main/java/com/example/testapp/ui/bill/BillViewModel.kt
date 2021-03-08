package com.example.testapp.ui.bill

import com.example.testapp.data.repository.BillRepository
import com.example.testapp.ui.base.BaseViewModel

class BillViewModel(
    private val repository: BillRepository
) : BaseViewModel(repository) {
}