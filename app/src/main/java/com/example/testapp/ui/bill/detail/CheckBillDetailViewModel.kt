package com.example.testapp.ui.bill.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.data.db.entities.Bill
import com.example.testapp.data.network.Resource
import com.example.testapp.data.repository.CheckBillDetailRepository
import com.example.testapp.data.response.BillDetailResponse
import com.example.testapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class CheckBillDetailViewModel(
    private val repository: CheckBillDetailRepository
) : BaseViewModel(repository) {

    private val _billDetails: MutableLiveData<Resource<BillDetailResponse>> = MutableLiveData()
    val billDetail : LiveData<Resource<BillDetailResponse>>
        get() = _billDetails

    lateinit var bill: Bill

    fun setData(bill : Bill) {
        this.bill = bill
        getBillDetails(this.bill.id)
    }

    fun getBillDetails(idBill: Int) {
        viewModelScope.launch {
            _billDetails.value = Resource.Loading
            _billDetails.value = repository.getBillDetails(idBill)
        }
    }
}