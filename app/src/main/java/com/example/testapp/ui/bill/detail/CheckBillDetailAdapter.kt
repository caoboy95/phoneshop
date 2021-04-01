package com.example.testapp.ui.bill.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.Constant
import com.example.testapp.data.db.entities.BillDetailsInfo
import com.example.testapp.data.repository.CheckBillDetailRepository
import com.example.testapp.databinding.CheckBillDetailFragmentBinding
import com.example.testapp.databinding.CheckBillDetailItemAdapterBinding
import com.example.testapp.ui.formatCurrency
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.*

class CheckBillDetailAdapter(val repository: CheckBillDetailRepository) : RecyclerView.Adapter<CheckBillDetailAdapter.CheckBillDetailHolder>() {

    private lateinit var billDetailsInfo: List<BillDetailsInfo>

    fun setData(billDetailsInfo: List<BillDetailsInfo>) {
        this.billDetailsInfo = billDetailsInfo
        notifyDataSetChanged()
    }

    class CheckBillDetailHolder(val binding: CheckBillDetailItemAdapterBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(billDetailsInfo: BillDetailsInfo) {
            binding.billDetailsInfo = billDetailsInfo
            val uri = Constant.URL_IMAGE+"product/"+billDetailsInfo.image.link
            Picasso.get().load(uri).into(binding.imageViewProduct)
            binding.textViewProductPrice.text = formatCurrency(billDetailsInfo.price)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckBillDetailHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CheckBillDetailItemAdapterBinding.inflate(inflater,parent,false)
        return CheckBillDetailHolder(binding)
    }

    override fun onBindViewHolder(holder: CheckBillDetailHolder, position: Int) {
        holder.bind(billDetailsInfo[position])
    }

    override fun getItemCount(): Int = billDetailsInfo.size
}