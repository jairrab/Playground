package com.example.temp.fragments.processdeath

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.navigation.fragment.findNavController
import com.example.temp.R
import com.example.temp.base.BaseFragment
import com.example.temp.base.BaseViewModel
import com.example.temp.databinding.ProcessDeathReceiverBinding
import com.example.temp.databinding.ProcessDeathSenderBinding
import com.github.jairrab.viewbindingutility.viewBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

class ProcessDeathTestReceiver : BaseFragment(R.layout.process_death_receiver) {
    private val binding by viewBinding { ProcessDeathReceiverBinding.bind(it) }
    private val viewModel by viewModels<ProcessDeathTestViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.text1Ld.observe(viewLifecycleOwner){
            binding.test1.text = it
        }

        viewModel.text2Ld.observe(viewLifecycleOwner){
            binding.test2.text = it
        }

        binding.test2.setOnClickListener {
            viewModel.test()
        }
    }
}

class ProcessDeathTestSender : BaseFragment(R.layout.process_death_sender) {
    private val binding by viewBinding { ProcessDeathSenderBinding.bind(it) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.test.setOnClickListener {
            findNavController().navigate(
                ProcessDeathTestSenderDirections.actionProcessDeathSenderToProcessDeathReceiver(
                    data1 = "Using Args",
                )
            )
        }
    }
}

@HiltViewModel
class ProcessDeathTestViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel(savedStateHandle) {

    private val stringArgsLd = savedStateHandle.getLiveData<String>("data_1")

    val text1Ld = Transformations.map(stringArgsLd) { it }

    val text2Ld = MutableLiveData<String>()

    init {
        Timber.v("VM Initialized")
    }

    fun test() {
        savedStateHandle.set("data_1","Using Args Reset")
        text2Ld.value = "Not using args"
    }
}