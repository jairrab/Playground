package com.example.temp.fragments.safeargs

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.temp.R
import com.example.temp.base.BaseFragment
import com.example.temp.databinding.SafeArgsSenderBinding
import com.github.jairrab.viewbindingutility.viewBinding

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