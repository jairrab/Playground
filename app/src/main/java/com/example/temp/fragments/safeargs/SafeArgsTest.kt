package com.example.temp.fragments.safeargs

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.temp.R
import com.example.temp.base.BaseFragment
import com.example.temp.base.BaseViewModel
import com.example.temp.databinding.SafeArgsSenderBinding
import com.github.jairrab.viewbindingutility.viewBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import javax.inject.Inject

class SafeArgsReceiver : BaseFragment(R.layout.safe_args_receiver) {
    private val args: SafeArgsReceiverArgs by navArgs()
    private val viewModel by viewModels<SafeArgsReceiverViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val a = args.data1
        val b = args.data2
        Timber.v(a)

        viewModel.test()
    }
}

class SafeArgsSender : BaseFragment(R.layout.safe_args_sender) {
    private val binding by viewBinding { SafeArgsSenderBinding.bind(it) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.test.setOnClickListener {
            findNavController().navigate(
                SafeArgsSenderDirections.actionHomeViewToOther(
                    data1 = "aaaaaa",
                    data2 = 23,
                )
            )
        }
    }
}

@HiltViewModel
class SafeArgsReceiverViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel(savedStateHandle) {

    private val args: SafeArgsReceiverArgs by navArgs()

    init {
        val a = args.data1
        val b = args.data2
        Timber.v(a)
    }

    fun test() {

    }
}

@Parcelize
data class ParcelData1(
    val a: Int,
    val b: String,
) : Parcelable