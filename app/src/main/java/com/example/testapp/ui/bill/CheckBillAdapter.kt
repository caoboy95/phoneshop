package com.example.testapp.ui.bill

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.R
import com.example.testapp.data.db.entities.Bill
import com.example.testapp.data.db.entities.BillAndQuantity
import com.example.testapp.databinding.CheckBillItemAdapterBinding
import com.example.testapp.ui.formatCurrency
import java.text.NumberFormat
import java.util.*

class CheckBillAdapter : RecyclerView.Adapter<CheckBillAdapter.CheckBillViewHolder>() {

    private lateinit var clickListener: BillClickListener
    private lateinit var billAndQuantity: List<BillAndQuantity>

    fun setData(billAndQuantity: List<BillAndQuantity>) {
        this.billAndQuantity =  billAndQuantity
        notifyDataSetChanged()
    }

    class CheckBillViewHolder(val binding: CheckBillItemAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(billAndQuantity: BillAndQuantity, clickListener: BillClickListener) {
            binding.textViewCheckBillItemId.text = ("#${billAndQuantity.bill.id}")
            binding.textViewCheckBillItemDate.text = billAndQuantity.bill.date_order
            binding.textViewCheckBillItemTotal.text = formatCurrency(billAndQuantity.bill.total)
            binding.textViewCheckBillItemQuantity.text = billAndQuantity.quantity.toString()

            binding.textViewCheckBillItemStatus.text = when(billAndQuantity.bill.status) {
                0 -> {
                    binding.textViewCheckBillItemStatus.setBackgroundColor(Color.parseColor("#D3A419"))
                    "Chưa Giao Hàng" }
                1 -> {
                    binding.textViewCheckBillItemStatus.setBackgroundColor(Color.parseColor("#D80000"))
                    "Đã Hủy" }
                2 -> {
                    binding.textViewCheckBillItemStatus.setBackgroundColor(Color.parseColor("#06CF0E"))
                    "Đã Giao Hàng" }
                else -> "Không"
            }
            binding.buttonCheckBillDetail.setOnClickListener {
                clickListener.onBillClickListener(billAndQuantity.bill)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckBillViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding : CheckBillItemAdapterBinding = CheckBillItemAdapterBinding.inflate(inflater, parent, false)
        return CheckBillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CheckBillViewHolder, position: Int) {
        holder.bind(billAndQuantity[position], clickListener)
    }

    override fun getItemCount() = billAndQuantity.size

    fun setOnBillClickListener(clickListener: BillClickListener) {
        this.clickListener = clickListener
    }

    interface BillClickListener {
        fun onBillClickListener(bill: Bill)
    }
}