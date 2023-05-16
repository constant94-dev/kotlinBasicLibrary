package com.example.app_mywebbrowser

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction

import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen()
        }
    }
}

@Composable
fun HomeScreen() {
    Scaffold(
        content = {
            it
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),

            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text(text = "https://") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {})
                )

                Spacer(modifier = Modifier.height(16.dp))

                MyWebView()
            }

        },
        topBar = {
            TopAppBar(
                title = { Text(text = "나만의 웹 브라우저") },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "forward",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    )
} // HomeScreen function end

@Composable
fun MyWebView() {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            // 초기의 WebView 를 실행하고 싶을 때 'it' Context 값이 들어오기 때문에 WebView 객체의 인자로 전달한다.
            // update 가 발생하지 않으면 여기가 계속 실행된다.
            WebView(it).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                loadUrl("https://google.com")
            }
        },
        update = {
            // 리컴포지션이 발생할 때 실행된다.
        })
}