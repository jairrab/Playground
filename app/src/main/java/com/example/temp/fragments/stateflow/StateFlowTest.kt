package com.example.temp.fragments.stateflow

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.temp.R
import com.example.temp.base.BaseFragment
import com.example.temp.base.BaseViewModel
import com.example.temp.databinding.StateFlowLauncherBinding
import com.example.temp.databinding.StateFlowTestBinding
import com.github.jairrab.viewbindingutility.viewBinding
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

class StateFlowLauncher : BaseFragment(R.layout.state_flow_launcher) {
    private val binding by viewBinding { StateFlowLauncherBinding.bind(it) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.test.setOnClickListener {
            findNavController().navigate(R.id.state_flow_test)
        }
    }
}

@InternalCoroutinesApi
class StateFlowTest : BaseFragment(R.layout.state_flow_test) {
    private val binding by viewBinding { StateFlowTestBinding.bind(it) }
    private val viewModel by activityViewModels<StateFlowTestViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->
                when (state) {
                    is LatestNewsUiState.Error -> Timber.e(state.exception)
                    is LatestNewsUiState.Success -> {
                        Timber.v("UI StateFlow: ${state.news}")
                    }
                }
            }
        }

        viewModel.liveDataState.observe(viewLifecycleOwner) {
            Timber.v("UI  LiveData: ${it.news}")
        }

        binding.test.setOnClickListener {
            findNavController().navigate(R.id.safe_args_sender)
        }
    }
}

@InternalCoroutinesApi
class StateFlowTestViewModel(
    savedStateHandle: SavedStateHandle
) : BaseViewModel(savedStateHandle) {
    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(LatestNewsUiState.Success(""))

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<LatestNewsUiState> = _uiState

    val liveDataState = MutableLiveData(LatestNewsUiState.Success(""))

    init {
        viewModelScope.launch {
            NewsRepository().favoriteLatestNews()
                // Update View with the latest favorite news
                // Writes to the value property of MutableStateFlow,
                // adding a new element to the flow and updating all
                // of its collectors
                .collect { favoriteNews ->
                    Timber.v("ViewModel: $favoriteNews")
                    _uiState.value = LatestNewsUiState.Success(favoriteNews)
                    liveDataState.value = LatestNewsUiState.Success(favoriteNews)
                }
        }
    }
}

class NewsRepository() {
    fun favoriteLatestNews(): Flow<String> {
        return flow {
            var counter = 0
            while (true) {
                val value = counter.toString()
                emit(value)
                counter += 1
                delay(1000)
            }
        }
    }
}


// Represents different states for the LatestNews screen
sealed class LatestNewsUiState {
    data class Success(val news: String) : LatestNewsUiState()
    data class Error(val exception: Throwable) : LatestNewsUiState()
}