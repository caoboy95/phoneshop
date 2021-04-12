package com.example.testapp.ui.base

import androidx.lifecycle.ViewModel
import com.example.testapp.data.repository.BaseRepository

abstract class BaseViewModel(
    private val repository: BaseRepository
): ViewModel() {
}