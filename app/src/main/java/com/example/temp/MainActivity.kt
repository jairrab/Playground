package com.example.temp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.temp.fragments.stateflow.LatestNewsUiState
import com.example.temp.fragments.stateflow.StateFlowTestViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@InternalCoroutinesApi
class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<StateFlowTestViewModel>()
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        job = lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is LatestNewsUiState.Error -> Timber.e(state.exception)
                    is LatestNewsUiState.Success -> {
                        Timber.v("UI StateFlow: ${state.news}")
                    }
                }
            }
        }

        viewModel.liveDataState.observe(this) {
            Timber.v("UI  LiveData: ${it.news}")
        }
    }

    override fun onStop() {
        super.onStop()
        job?.cancel() //not needed if using launchWhenStarted
    }
}