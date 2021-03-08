package com.example.testapp.ui.bill

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.data.network.Resource
import com.example.testapp.data.repository.CheckBillRepository
import com.example.testapp.data.response.BillResponse
import com.example.testapp.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class CheckBillViewModel(
    val repository: CheckBillRepository
) : BaseViewModel(repository) {

    private val _bills : MutableLiveData<Resource<BillResponse>> = MutableLiveData()
    val bills: LiveData<Resource<BillResponse>>
        get() = _bills
    private lateinit var phone: String

    fun setData(phone: String){
        this.phone=phone
        getBills()
    }

    fun getBills(){
        viewModelScope.launch {
            _bills.value=Resource.Loading
            _bills.value =  repository.getBills(phone)
        }
    }
}