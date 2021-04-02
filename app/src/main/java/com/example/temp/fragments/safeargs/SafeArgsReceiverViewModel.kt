package com.example.temp.fragments.safeargs

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.example.temp.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SafeArgsReceiverViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel(savedStateHandle) {

    private val args: SafeArgsReceiverArgs by navArgs()

    init {
        val a = args.data1
        val b = args.data2
        Log.v("TAG", a)
    }

    fun test(){

    }
}
