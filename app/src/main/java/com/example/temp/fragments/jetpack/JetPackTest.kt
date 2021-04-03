package com.example.temp.fragments.jetpack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class JetPackView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PreviewGreeting()
        }
    }
}

@Preview
@Composable
fun PreviewGreeting() {
    Greeting("Android")
}

@Composable
fun Greeting(name: String) {
    Row() {
        Text (text = "Hello $name!")
        Text (text = "How are you?")
    }

}