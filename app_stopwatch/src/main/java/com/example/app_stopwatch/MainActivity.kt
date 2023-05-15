@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.app_stopwatch

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton

import androidx.compose.material3.Scaffold

import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

import java.util.Timer
import kotlin.concurrent.timer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            /* 아래의 작성한 ViewModel(기능구성) 과 Composable(화면구성) 을 연결해요 */
            val viewModel = viewModel<MainViewModel>()

            val sec = viewModel.sec.value
            val milli = viewModel.milli.value
            val isRunning = viewModel.isRunning.value
            val lapTimes = viewModel.lapTimes.value

            MainScreen(
                sec = sec,
                milli = milli,
                isRunning = isRunning,
                lapTimes = lapTimes,
                onReset = { viewModel.reset() },
                onToggle = { running ->
                    if (running) {
                        viewModel.pause()
                    } else {
                        viewModel.start()
                    }
                },
                onLapTime = { viewModel.recordLapTime() }
            ) // MainScreen end
        } // setContent end
    }
}

// MainActivity 에서 사용할 기능
class MainViewModel : ViewModel() {
    private var time = 0 // 중간의 멈춘 상태일 때 '초'
    private var timerTask: Timer? = null // 시간이 흐르게 하는 효과

    // '재생 여부' 변수
    private val _isRunning = mutableStateOf(false)
    val isRunning: State<Boolean> = _isRunning

    // '초' 변수
    private val _sec = mutableStateOf(0)
    val sec: State<Int> = _sec

    // '밀리 초' 변수
    private val _milli = mutableStateOf(0)
    val milli: State<Int> = _milli

    // '중간의 시간이 얼마나 흘렀는지 확인할 수 있는 랩 타임' 변수
    private val _lapTimes = mutableStateOf(mutableListOf<String>())
    val lapTimes: State<List<String>> = _lapTimes

    // 처음 시작하는 랩 변수
    private var lap = 1

    /* 스톱워치 시작 기능 */
    fun start() {
        _isRunning.value = true // 재생 하는 상태

        timerTask = timer(period = 10) { // 0.01초씩 흘러감
            time++ // 1씩 증가해요
            _sec.value = time / 100 // '현재 time' 을 100 으로 나눈 몫 = '초'
            _milli.value = time % 100 // '현재 time' 을 100 으로 나눈 나머지 = '밀리 초'
        }
    }

    /* 스톱워치 일시정지 기능 */
    fun pause() {
        _isRunning.value = false // 재생 안하는 상태
        timerTask?.cancel() // timer 동작을 취소해요
    }

    /* 스톱워치 초기화 기능 */
    fun reset() {
        timerTask?.cancel() // timer 동작을 취소해요

        time = 0 // 'time' 변수 초기화
        _isRunning.value = false // 재생 안하는 상태
        _sec.value = 0 // '초' 변수를 초기화
        _milli.value = 0 // '밀리 초' 변수를 초기화

        _lapTimes.value.clear() // 지금까지 추가한 랩 타임 리스트를 초기화해요
        lap = 1 // 지금까지 체크한 랩 타임 횟수를 초기화해요
    }

    /* 스톱워치 시간기록 기능 */
    fun recordLapTime() {
        // 중간의 확인하려는 랩 타임을 리스트 형태로 추가해요
        _lapTimes.value.add(0, "$lap LAP: ${sec.value}.${milli.value}")
        lap++ // 랩 타임을 확인할 때마다 체크한 랩 타임 횟수를 1씩 증가시켜요
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    sec: Int, // '초'
    milli: Int, // '밀리 초'
    isRunning: Boolean, // '재생여부'
    lapTimes: List<String>, // '랩 타임 리스트'
    onReset: () -> Unit, // '초기화'
    onToggle: (Boolean) -> Unit, // '재생/일시정지'
    onLapTime: () -> Unit, // '현재 확인되는 랩 타임'
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "StopWatch") })
        }) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(text = "$sec", fontSize = 100.sp)
                Text(text = "$milli")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                lapTimes.forEach { lapTime ->
                    Text(text = lapTime)
                }
            }

            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FloatingActionButton(
                    onClick = { onReset() },
                    Modifier.background(Color.Red),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_refresh_24),
                        contentDescription = "reset"
                    )
                }

                FloatingActionButton(
                    onClick = { onToggle(isRunning) },
                    Modifier.background(Color.Green),
                ) {
                    Image(
                        painter = painterResource(
                            id =
                            if (isRunning) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24
                        ),
                        contentDescription = "start/pause"
                    )
                }

                Button(onClick = { onLapTime() }) {
                    Text(text = "랩 타임")
                }
            }
        }
    }

}