package com.example.temp.fragments.ktortest

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.example.temp.R
import com.example.temp.base.BaseFragment
import com.example.temp.databinding.KtorTestBinding
import com.example.temp.fragments.ktortest.Constants.BASE_URL
import com.github.jairrab.viewbindingutility.viewBinding
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.observer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import timber.log.Timber

class KtorTest : BaseFragment(R.layout.ktor_test) {
    private val binding by viewBinding { KtorTestBinding.bind(it) }

    private val userApi = UserApi(ktorHttpClient)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.test.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val data = userApi.getData(1)
                Timber.v("Received data: $data")
            }

        }
    }
}

private const val TIME_OUT = 60_000

private val ktorHttpClient = HttpClient(OkHttp) {

    install(JsonFeature) {
        serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    install(HttpTimeout) {
        connectTimeoutMillis = 3000
        socketTimeoutMillis = 3000
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                Log.v("Logger Ktor =>", message)
            }

        }
        level = LogLevel.ALL
    }

    install(ResponseObserver) {
        onResponse { response ->
            Log.d("HTTP status:", "${response.status.value}")
        }
    }

    install(DefaultRequest) {
        header(HttpHeaders.ContentType, ContentType.Application.Json)
    }
}

class UserApi(private val client: HttpClient) {
    suspend fun getData(
        userId: Int
    ): Response = client.get("$BASE_URL$userId")

    /*suspend fun saveUser(user: Response) {
        client.post<Response>("$BASE_URL") {
            body = user
        }
    }*/
}

@Serializable
data class Response(
    @SerialName("userId") val userId: Int,
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("completed") val completed: Boolean,
)

object Constants {
    const val BASE_URL = "https://jsonplaceholder.typicode.com/todos/"
}