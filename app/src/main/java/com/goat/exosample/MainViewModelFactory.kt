package com.goat.exosample

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

  override fun <T : ViewModel> create(modelClass: Class<T>): T =
    MainViewModel(context) as T
}