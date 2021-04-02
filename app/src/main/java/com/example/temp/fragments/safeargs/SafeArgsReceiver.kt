package com.example.temp.fragments.safeargs

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.temp.R
import com.example.temp.base.BaseFragment

class SafeArgsReceiver : BaseFragment(R.layout.safe_args_receiver) {
    private val args: SafeArgsReceiverArgs by navArgs()
    private val viewModel by viewModels<SafeArgsReceiverViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val a = args.data1
        val b = args.data2
        Log.v("TAG", a)

        viewModel.test()
    }
}