package com.example.testapp.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.testapp.R
import com.example.testapp.data.network.Resource
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import java.text.NumberFormat
import java.util.*

fun <T> DataSnapshot.getDataValue(dataClass: Class<T>) : List<T> {
    val data = mutableListOf<T>()
    if (this.exists()) {
        this.children.forEach { dataSnapshot ->
            dataSnapshot?.getValue(dataClass)?.let {
                data.add(it)
            }
        }
    }
    return data
}

fun formatCurrency(currency: Long): String = NumberFormat.getCurrencyInstance(Locale("vn","VN")).format(currency)

fun <A: Activity> Activity.startNewActivity(activity: Class<A>) {
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}

fun View.visible(isVisible: Boolean) {
    visibility = if(isVisible) View.VISIBLE else View.GONE
}

fun View.enable(enabled: Boolean) {
    isEnabled = enabled
    alpha = if(enabled) 1f else 0.5f
}

fun Fragment.handleApiError(
    failure: Resource.Failure,
    retry: (()-> Unit)? = null
){
    when {
        failure.isNetworkError -> requireView().snackbar("Please check your internet connection",retry)
        failure.errorCode == 404 -> {
                requireView().snackbar("There isn't Api")
        }
        else -> {
            requireView().snackbar(failure.message as String)
        }
    }
}

fun Context.getResColor(colorID: Int) : Int {
    return ContextCompat.getColor(this, colorID)
}

fun View.snackbar(message: String, action: (() -> Unit)? = null) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).apply {
        setBackgroundTint(this.view.context.getResColor(R.color.colorPrimary))
        action?.let {
            setAction("Retry") {
                action()
            }
            return
        }
        setAction("OK") {
            dismiss()
        }
    }.show()
}