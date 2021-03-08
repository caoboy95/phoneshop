package com.example.testapp.ui.bill.detail

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapp.R
import com.example.testapp.data.db.entities.BillDetailsInfo
import com.example.testapp.data.network.NetworkConnectionInterceptor
import com.example.testapp.data.network.ProductApi
import com.example.testapp.data.network.Resource
import com.example.testapp.data.repository.CheckBillDetailRepository
import com.example.testapp.data.response.BillDetailResponse
import com.example.testapp.databinding.CheckBillDetailFragmentBinding
import com.example.testapp.ui.base.BaseFragment
import com.example.testapp.ui.handleApiError

class CheckBillDetailFragment : BaseFragment<CheckBillDetailViewModel,CheckBillDetailFragmentBinding,CheckBillDetailRepository>() {

    val safeArgs: CheckBillDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(safeArgs.bill != null)
            viewModel.setData(safeArgs.bill)
        viewModel.billDetail.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success -> updateUI(it.value)
                is Resource.Loading -> {
//                    binding.progressBar.visible(true)
                }
                is Resource.Failure -> {
//                    binding.progressBar.visible(false)
                    this.handleApiError(it)
                }
            }
        })
    }

    fun updateUI(billDetail: BillDetailResponse){
        binding.textViewCheckBillDetailId.text = viewModel.bill.id.toString()
        binding.textViewCheckBillDetailDate.text = viewModel.bill.date_order
        binding.textViewCheckBillDetailStatus.text = when(viewModel.bill.status){
            0 -> {
                binding.textViewCheckBillDetailStatus.setBackgroundColor(Color.parseColor("#D3A419"))
                "Chưa Giao Hàng" }
            1 -> {
                binding.textViewCheckBillDetailStatus.setBackgroundColor(Color.parseColor("#D80000"))
                "Đã Hủy" }
            2 -> {
                binding.textViewCheckBillDetailStatus.setBackgroundColor(Color.parseColor("#06CF0E"))
                "Đã Giao Hàng" }
            else -> "Không"
        }
        initRecyclerView(billDetail.billDetailsInfo)
    }

    fun initRecyclerView(billDetailsInfo: List<BillDetailsInfo>){
        val mAdapter = CheckBillDetailAdapter()
        mAdapter.setData(billDetailsInfo)
        binding.recyclerViewCheckBillDetailItem.apply {
            layoutManager= LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            setHasFixedSize(true)
            adapter= mAdapter
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): CheckBillDetailFragmentBinding = CheckBillDetailFragmentBinding.inflate(inflater, container,false)

    override fun getViewModel()= CheckBillDetailViewModel::class.java

    override fun getFragmentRepository(networkConnectionInterceptor: NetworkConnectionInterceptor): CheckBillDetailRepository =
        CheckBillDetailRepository(remoteDataSource.buildApi(ProductApi::class.java,networkConnectionInterceptor))


}