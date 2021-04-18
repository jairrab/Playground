package com.example.temp.fragments.patterns

import android.app.Application
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import com.example.temp.R
import com.example.temp.base.BaseFragment
import com.example.temp.base.BaseViewModel
import com.example.temp.fragments.patterns.ApiFlowUseCase.Response
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

//region VIEW (ANDROID FRAMEWORK)

//region HILT DI
@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {
    @Binds
    abstract fun repository(repositoryImpl: RepositoryImpl): Repository

    @Binds
    abstract fun remoteSource(webSource: WebSource): RemoteSource

    @Binds
    abstract fun localSource(localSourceImpl: LocalSourceImpl): LocalSource

    @Binds
    abstract fun simpleStorage(appPreferences: SimpleStorageImpl): SimpleStorage
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun providesSharedPreferences(application: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }
}
//endregion

//region FRAGMENTS
@AndroidEntryPoint
@WithFragmentBindings
class PatternExercise : BaseFragment(R.layout.patterns_exercise) {
    private val viewModel by viewModels<MyViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.usersLd.observe(viewLifecycleOwner, Observer {
            //do something with the list
        })
    }
}
//endregion

//endregion

//region PRESENTATION (ANDROID FRAMEWORK)
@HiltViewModel
class MyViewModel @Inject constructor(
    private val getUsers: GetUsers,
    private val userId: UserId,
    private val refreshUsers: RefreshUsers,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(savedStateHandle) {
    private val _usersLd = MutableLiveData<List<User>>()
    val usersLd = Transformations.map(_usersLd) { it }

    init {
        viewModelScope.launch {
            getUsers.execute(12).collect { list ->
                //do something with the list
            }
        }

        updateUsers()
    }

    private fun updateUsers() = viewModelScope.launch {
        refreshUsers.execute(userId.data).collect {
            when (it) {
                is Response.Failed -> {
                    //do something
                }
                is Response.Fetching -> {
                    //do something
                }
                is Response.Success -> {
                    //do something
                }
            }
        }
    }
}
//endregion

//region DOMAIN - PURE KOTLIN

//region BUSINESS MODELS
data class User(
    val name: String = "",
    val age: Int = 20,
)
//endregion

//region USE-CASE BASE CLASSES

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

/** this use case is used for collecting flowable information from local data sources **/
abstract class DataFlowUseCase<Input, Output> {
    abstract fun execute(params: Input): Flow<Output>
}

abstract class KeyValuePairUseCase<Data> {
    abstract var data: Data
}

//endregion

//region USE CASES
class GetUsers @Inject constructor(
    private val repository: Repository
) : DataFlowUseCase<Int, List<User>>() {
    override fun execute(params: Int) = repository.getUsers(params)
}

class RefreshUsers @Inject constructor(
    private val repository: Repository
) : ApiFlowUseCase<Int, Unit>() {
    override fun execute(params: Int) = repository.refreshUsers(params)
}

class UserId @Inject constructor(
    private val repository: Repository
) : KeyValuePairUseCase<Int>() {
    override var data: Int
        get() = repository.userId
        set(value) {
            repository.userId = value
        }
}
//endregion

//region DATA INTERFACE
interface Repository {
    var userId: Int
    fun getUsers(id: Int): Flow<List<User>>
    fun refreshUsers(id: Int): Flow<Response<Unit>>
}
//endregion

//endregion

//region DATA - PURE KOTLIN
class RepositoryImpl @Inject constructor(
    private val localSource: LocalSource,
    private val remoteSource: RemoteSource,
    private val simpleStorage: SimpleStorage,
) : Repository {
    override var userId: Int
        get() = simpleStorage.userId
        set(value) {
            simpleStorage.userId = value
        }

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

//region SOURCES
/** remote repository interface definition **/
interface RemoteSource {
    suspend fun getUsers(id: Int): List<User>
}

/** local repository interface definition **/
interface LocalSource {
    fun getUsers(id: Int): Flow<List<User>>
    suspend fun saveUsers(users: List<User>)
}

interface SimpleStorage {
    var userId: Int
}
//endregion

//region DATA UTILS
interface DomainMapper<T, U> {
    fun mapToDomain(data: T): U
    fun mapToDto(data: U): T
}
//endregion

//endregion

//region REMOTE - RETROFIT (ANDROID FRAMEWORK) / KTOR (PURE KOTLIN)
class WebSource @Inject constructor(
    private val webService: WebService,
) : RemoteSource {
    override suspend fun getUsers(id: Int): List<User> {
        return webService.getUsers(id).map { it.toDomain() }
    }
}

class WebService @Inject constructor() {
    suspend fun getUsers(id: Int): List<UserDto> {
        delay(1000)
        return emptyList()
    }
}

data class UserDto(
    val name: String = "",
    val age: Int = 20,
)

fun UserDto.toDomain(): User {
    return User(name, age)
}
//endregion

//region CACHE

//region ROOM (ANDROID FRAMEWORK) / SQL-DELIGHT (PURE KOTLIN)
class LocalSourceImpl @Inject constructor() : LocalSource {
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

//region SHARED-PREFERENCE (ANDROID FRAMEWORK)
class SimpleStorageImpl @Inject constructor(
    private val preferences: SharedPreferences
) : SimpleStorage {
    override var userId: Int
        get() = preferences.getInt(USER_ID, -1)
        set(value) = preferences.edit { putInt(USER_ID, value) }

    companion object {
        private const val USER_ID = "USER_ID"
    }
}
//endregion

//endregion