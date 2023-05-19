package com.example.app_xylophone

import android.app.Application
import android.content.pm.ActivityInfo
import android.media.SoundPool
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>() // Activity 하나만 사용할 때는 'ViewModel' 관련 추가 라이브러리 없이 사용할 수 있다.

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        super.onCreate(savedInstanceState)
        setContent {
            XylophoneScreen(viewModel = viewModel)
        }
    }
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val soundPool =
        SoundPool.Builder().setMaxStreams(8).build() // 도,레,미,파,솔,라,시,도 최대 8개 동시 플레이를 위한 세팅

    private val sounds = listOf(
        // 'load' 파라미터 값 중 'context' 값이 필요하므로 ViewModel -> AndroidViewModel 로 변경
        soundPool.load(application.applicationContext, R.raw.do1, 1),
        soundPool.load(application.applicationContext, R.raw.re, 1),
        soundPool.load(application.applicationContext, R.raw.mi, 1),
        soundPool.load(application.applicationContext, R.raw.fa, 1),
        soundPool.load(application.applicationContext, R.raw.sol, 1),
        soundPool.load(application.applicationContext, R.raw.la, 1),
        soundPool.load(application.applicationContext, R.raw.si, 1),
        soundPool.load(application.applicationContext, R.raw.do2, 1),
    )

    // 화면에서 이벤트되는 음계 인덱스를 받아서 음악을 플레이하는 기능
    fun playSound(index: Int) {
        val play = soundPool.play(sounds[index], 1f, 1f, 0, 0, 1f)
    }

    // soundPool 사용 안할 때 해지 해 주어야 한다
    override fun onCleared() {
        soundPool.release() // 메모리에서 해지된다.
        super.onCleared()
    }
}

@Composable
fun XylophoneScreen(viewModel: MainViewModel) {
    // 'Pair' 두 가지 데이터 타입을 하나로 묶는데 편하다.
    val keys = listOf(
        Pair("도", Color.Red),
        Pair("레", Color(0xFFFF9800)),
        Pair("미", Color(0xFFFFC107)),
        Pair("파", Color(0xFF8BC34A)),
        Pair("솔", Color(0xFF2196F3)),
        Pair("라", Color(0xFF3F51B5)),
        Pair("시", Color(0xFF673AB7)),
        Pair("도", Color.Red),
    )

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        keys.forEachIndexed { index, key ->
            val padding = (index + 2) * 8 // 16, 24, 32 ....
            KeyBoard(
                modifier = Modifier
                    .padding(top = padding.dp, bottom = padding.dp)
                    .clickable {
                        viewModel.playSound(index)
                    },
                text = key.first,
                color = key.second,
            )
        }
    }
}

@Composable
fun KeyBoard(
    modifier: Modifier,
    text: String,
    color: Color,
) {
    Box(
        modifier = modifier // 외부에서 정의된 'modifier' 값을 여기서 사용하는 것
            .width(50.dp)
            .fillMaxHeight()
            .background(color = color)
    ) {
        Text(
            text = text,
            style = TextStyle(color = Color.White, fontSize = 20.sp),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}