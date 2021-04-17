package com.example.temp.fragments.patterns

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.example.temp.base.BaseViewModel
import com.example.temp.fragments.patterns.ApiFlowUseCase.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

//region VIEW
class MyView : Fragment() {
    private lateinit var viewModel: MyViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.usersLd.observe(viewLifecycleOwner, Observer {
            //do something with the list
        })
    }
}
//endregion

//region PRESENTATION
class MyViewModel(
    private val getUsers: GetUsers,
    private val refreshUsers: RefreshUsers,
    private val simpleStorage: SimpleStorage,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(savedStateHandle) {

    private val _usersLd = MutableLiveData<List<User>>()
    val usersLd: LiveData<List<User>> = Transformations.map(_usersLd) { it }

    init {
        viewModelScope.launch {
            getUsers.execute(12).collect { list ->
                //do something with the list
            }
        }

        updateUsers()
    }

    private fun updateUsers() = viewModelScope.launch {
        refreshUsers.execute(simpleStorage.userId).collect {
            when (it) {
                is Response.Failed -> TODO()
                is Response.Fetching -> TODO()
                is Response.Success -> TODO()
            }
        }
    }
}
//endregion

//region DOMAIN

//region BUSINESS MODELS
data class User(
    val name: String = "",
    val age: Int = 20,
)
//endregion

//region USE-CASE BASE CLASSES

//region API FLOW USE CASE
/** this use case type provides state feedback for API calls **/
abstract class ApiFlowUseCase<Input, Output> {
    abstract fun execute(params: Input): Flow<Response<Output>>

    /** Response definition model **/
    sealed class Response<T> {
        class Success<T>(val data: T) : Response<T>()
        class Fetching<T>() : Response<T>()
        class Failed<T>(val e: Exception) : Response<T>()
    }
}
//endregion

//region DATA FLOW USE CASE
/** this use case is used for collecting flowable information from local data sources **/
abstract class DataFlowUseCase<Input, Output> {
    abstract fun execute(params: Input): Flow<Output>
}
//endregion

//endregion

//region USE CASES
class GetUsers @Inject constructor(
    private val repo: Repository
) : DataFlowUseCase<Int, List<User>>() {
    override fun execute(params: Int) = repo.getUsers(params)
}

class RefreshUsers @Inject constructor(
    private val repo: Repository
) : ApiFlowUseCase<Int, Unit>() {
    override fun execute(params: Int) = repo.refreshUsers(params)
}
//endregion

//region STORAGE INTERFACE
interface Repository {
    fun getUsers(id: Int): Flow<List<User>>
    fun refreshUsers(id: Int): Flow<Response<Unit>>
}

interface SimpleStorage {
    var userId: Int
}
//endregion

//endregion

//region REPOSITORY
class RepoImpl @Inject constructor(
    private val localSource: LocalSource,
    private val remoteSource: RemoteSource,
) : Repository {
    override fun getUsers(id: Int): Flow<List<User>> {
        return localSource.getUsers(id)
    }

    override fun refreshUsers(id: Int) = flow {
        try {
            emit(Response.Fetching())
            val user = remoteSource.getUsers(id)
            localSource.saveUsers(user)
            emit(Response.Success(Unit))
        } catch (e: Exception) {
            emit(Response.Failed<Unit>(e))
        }
    }
}

/** remote repository interface definition **/
interface RemoteSource {
    suspend fun getUsers(id: Int): List<User>
}

/** local repository interface definition **/
interface LocalSource {
    fun getUsers(id: Int): Flow<List<User>>
    suspend fun saveUsers(users: List<User>)
}
//endregion

//region REMOTE
class WebService : RemoteSource {
    override suspend fun getUsers(id: Int): List<User> {
        delay(1000)
        return emptyList()
    }
}
//endregion

//region CACHE
class RoomDb : LocalSource {
    override fun getUsers(id: Int): Flow<List<User>> {
        return flow {
            emit(emptyList())
        }
    }

    override suspend fun saveUsers(users: List<User>) {
        //save user
    }
}
//endregion

//region SIMPLE KEY-VALUE STORAGE
class AppPreferences @Inject constructor(
    private val preferences: SharedPreferences
) : SimpleStorage {
    override var userId: Int
        get() = preferences.getInt(USER_ID, -1)
        set(value) {
            preferences.edit { putInt(USER_ID, value) }
        }

    companion object {
        private const val USER_ID = "USER_ID"
    }
}
//endregion
