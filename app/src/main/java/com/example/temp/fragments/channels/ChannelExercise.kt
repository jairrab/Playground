package com.example.temp.fragments.channels

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.example.temp.R
import com.example.temp.base.BaseFragment
import com.example.temp.databinding.ChannelExerciseBinding
import com.github.jairrab.viewbindingutility.viewBinding
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import timber.log.Timber

class ChannelExercise : BaseFragment(R.layout.channel_exercise) {
    private val binding by viewBinding { ChannelExerciseBinding.bind(it) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.test.setOnClickListener {
            test()
        }
    }

    private fun test() {
        val channel = Channel<Int>()
        viewLifecycleOwner.lifecycleScope.launch {
            // this might be heavy CPU-consuming computation or async logic, we'll just send five squares
            for (x in 1..5) channel.send(x * x)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            // here we print five received integers:
            repeat(5) { Timber.v("${channel.receive()}") }
            Timber.v("Done!")
        }
    }
}