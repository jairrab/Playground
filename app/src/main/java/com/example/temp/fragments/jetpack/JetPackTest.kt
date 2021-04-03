package com.example.temp.fragments.jetpack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

class JetPackView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Greeting("Hello world!")
        }
    }

    @Composable
    fun Greeting(name: String) {
        Text (text = "Hello $name!")
    }
}