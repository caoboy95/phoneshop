package com.example.testapp.data.response

import com.example.testapp.data.db.entities.BillDetailsInfo

data class BillDetailResponse(
    val billDetailsInfo: List<BillDetailsInfo>
)