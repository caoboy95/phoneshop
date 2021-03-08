package com.example.testapp.ui

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import com.example.testapp.data.network.Resource
import com.google.android.material.snackbar.Snackbar

fun<A: Activity> Activity.startNewActivity(activity: Class<A>){
    Intent(this,activity).also{
        it.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}

fun View.visible(isVisible: Boolean){
    visibility = if(isVisible) View.VISIBLE else View.GONE
}

fun View.enable(enabled: Boolean){
    isEnabled = enabled
    alpha = if(enabled) 1f else 0.5f
}

fun Fragment.handleApiError(
    failure: Resource.Failure,
    retry: (()-> Unit)? = null
){
    when{
        failure.isNetworkError -> requireView().snackbar("Please check your internet connection",retry)
        failure.errorCode == 404 -> {

                requireView().snackbar("There isn't Api")

        }
        else ->{
            requireView().snackbar(failure.message as String)
        }
    }
}

fun View.snackbar(message: String, action: (() -> Unit)? = null){
    val snackbar = Snackbar.make(this,message, Snackbar.LENGTH_SHORT)
    action?.let {
        snackbar.setAction("Retry"){
            it()
        }
    }
    snackbar.show()
}