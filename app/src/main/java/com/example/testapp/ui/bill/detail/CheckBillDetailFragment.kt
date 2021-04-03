package com.example.testapp.ui.bill.detail

import android.graphics.Color
import android.os.Bundle
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
import com.example.testapp.data.repository.CheckBillDetailRepository
import com.example.testapp.data.response.BillDetailResponse
import com.example.testapp.databinding.CheckBillDetailFragmentBinding
import com.example.testapp.ui.base.BaseFragment
import com.example.testapp.ui.formatCurrency
import com.example.testapp.ui.visible

class CheckBillDetailFragment : BaseFragment<CheckBillDetailViewModel, CheckBillDetailFragmentBinding, CheckBillDetailRepository>() {

    private val safeArgs: CheckBillDetailFragmentArgs by navArgs()
    private var checkBillDetailAdapter: CheckBillDetailAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setData(safeArgs.bill)
        binding.progressBar.bringToFront()
        binding.progressBar.visible(true)
        viewModel.billDetailsFB.observe(viewLifecycleOwner, Observer {
            updateUI(it)
            binding.progressBar.visible(false)
        })
//        viewModel.billDetail.observe(viewLifecycleOwner, Observer {
//            when(it){
//                is Resource.Success -> updateUI(it.value)
//                is Resource.Loading -> {
////                    binding.progressBar.visible(true)
//                }
//                is Resource.Failure -> {
////                    binding.progressBar.visible(false)
//                    this.handleApiError(it)
//                }
//            }
//        })
    }

    private fun updateUI(billDetail: BillDetailResponse) {
        val title = "#${viewModel.bill.id}"
        binding.textViewCheckBillDetailId.text = title
        binding.textViewCheckBillDetailDate.text = viewModel.bill.date_order
        binding.textViewCheckBillDetailTotal.text = formatCurrency(viewModel.bill.total)
        binding.textViewCheckBillDetailStatus.text =
            when(viewModel.bill.status) {
                UNDELIVERED -> {
                    binding.textViewCheckBillDetailStatus.setBackgroundColor(Color.parseColor("#D3A419"))
                    context?.resources?.getString(R.string.undelivered) }
                CANCELLED -> {
                    binding.textViewCheckBillDetailStatus.setBackgroundColor(Color.parseColor("#D80000"))
                    "Đã Hủy" }
                DELIVERED -> {
                    binding.textViewCheckBillDetailStatus.setBackgroundColor(Color.parseColor("#06CF0E"))
                    "Đã Giao Hàng" }
                else -> "Không"
            }
        initRecyclerView(billDetail.billDetailsInfo)
    }

    private fun initRecyclerView(billDetailsInfo: List<BillDetailsInfo>) {
        checkBillDetailAdapter = CheckBillDetailAdapter(viewModel.getRepository())
        checkBillDetailAdapter?.setData(billDetailsInfo)
        binding.recyclerViewCheckBillDetailItem.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = checkBillDetailAdapter
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): CheckBillDetailFragmentBinding = CheckBillDetailFragmentBinding.inflate(inflater, container,false)

    override fun getViewModel()= CheckBillDetailViewModel::class.java

    override fun getFragmentRepository(networkConnectionInterceptor: NetworkConnectionInterceptor): CheckBillDetailRepository =
        CheckBillDetailRepository(remoteDataSource.buildApi(ProductApi::class.java,networkConnectionInterceptor))

    companion object {
        private const val UNDELIVERED = 0
        private const val DELIVERED = 2
        private const val CANCELLED = 1
    }

}