package com.example.temp.fragments.singleevent

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.example.temp.R
import com.example.temp.base.BaseFragment
import com.example.temp.base.BaseViewModel
import com.example.temp.databinding.PatternsExerciseBinding
import com.example.temp.event.LiveEvent
import com.github.jairrab.androidutilities.eventobserver.Event
import com.github.jairrab.viewbindingutility.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

//region VIEW (ANDROID FRAMEWORK)

//region FRAGMENTS
@AndroidEntryPoint
@WithFragmentBindings
class SingleEventExercise : BaseFragment(R.layout.patterns_exercise) {
    private val viewModel by viewModels<MyViewModel>()
    private val binding by viewBinding { PatternsExerciseBinding.bind(it) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.stateFlow.collect {
                Timber.v("Event observed stateFlow1 = $it")
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.stateFlow.collect {
                Timber.v("Event observed stateFlow2 = $it")
            }
        }

        viewModel.liveEvent.observe(viewLifecycleOwner, Observer {
            Timber.v("Event observed liveEvent1 = $it")
        })

        viewModel.eventLd.observe(viewLifecycleOwner, EventObserver {
            Timber.v("Event observed eventLd1 = $it")
        })

        viewModel.liveEvent.observe(viewLifecycleOwner, Observer {
            Timber.v("Event observed liveEvent2 = $it")
        })

        viewModel.eventLd.observe(viewLifecycleOwner, EventObserver {
            Timber.v("Event observed eventLd2 = $it")
        })*/

        /*viewLifecycleOwner.lifecycleScope.launch {
            viewModel.broadcastChannelFlow.collect {
                Timber.v("Event observed broadcastChannelFlow1 = $it")
            }
        }*/

        /*viewLifecycleOwner.lifecycleScope.launch {
            viewModel.broadcastChannelFlow.collect {
                Timber.v("Event observed broadcastChannelFlow2 = $it")
            }
        }*/

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sharedFlow.collect {
                Timber.v("Event observed sharedFlow1 = $it")
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sharedFlow.collect {
                Timber.v("Event observed sharedFlow2 = $it")
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.channelFlow.collect {
                Timber.v("Event observed channelFlow1 = $it")
            }
        }

        /*viewLifecycleOwner.lifecycleScope.launch {
            viewModel.channelFlow.collect {
                Timber.v("Event observed channelFlow2 = $it")
            }
        }*/

        binding.test.setOnClickListener {
            viewModel.test()
        }
    }
}
//endregion

//endregion

//region PRESENTATION (ANDROID FRAMEWORK)
@HiltViewModel
class MyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(savedStateHandle) {
    private val _stateFlow = MutableStateFlow("")
    val stateFlow: StateFlow<String> get() = _stateFlow

    private val _sharedFlow = MutableSharedFlow<String>(0)
    val sharedFlow: SharedFlow<String> get() = _sharedFlow

    private val _liveEvent = LiveEvent<String>()
    val liveEvent: LiveData<String> get() = _liveEvent

    private val _eventLd = MutableLiveData<Event<String>>()
    val eventLd: LiveData<Event<String>> get() = _eventLd

    private val channel = Channel<String>(Channel.BUFFERED)
    val channelFlow = channel.receiveAsFlow()

    @ExperimentalCoroutinesApi
    private val broadcastChannel = BroadcastChannel<String>(Channel.BUFFERED)

    @FlowPreview
    val broadcastChannelFlow = broadcastChannel.asFlow()

    init {
        test()
    }

    var num = 0

    @ExperimentalCoroutinesApi
    fun test() {
        num++
        _stateFlow.value = "Hello number $num"
        viewModelScope.launch { _sharedFlow.emit("Hello number $num") }
        _liveEvent.value = "Hello number $num"
        _eventLd.value = Event("Hello number $num")
        viewModelScope.launch { broadcastChannel.send("Hello number $num") }
        viewModelScope.launch { channel.send("Hello number A $num") }
    }
}
//endregion