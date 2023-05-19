package com.example.app_horizontalinstrument

import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        // 화면이 꺼지지 않게 하기
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // 화면이 가로 모드로 고정되게 하기
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        super.onCreate(savedInstanceState)

        lifecycle.addObserver(viewModel)
        setContent {
            TiltScreen(
                x = viewModel.x.value,
                y = viewModel.y.value,
            )
        }
    }
}

class MainViewModel(application: Application) : AndroidViewModel(application), SensorEventListener,
    LifecycleEventObserver {

    private val _x = mutableStateOf(0f)
    val x: State<Float> = _x

    private val _y = mutableStateOf(0f)
    val y: State<Float> = _y

    private val sensorManager by lazy {
        application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    fun registerSensor() {
        sensorManager.registerListener(
            this, // 클래스의 구현체로 'SensorEventListener' 를 사용하니 this 로 접근 가능
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), // 어떤 센서를 지정할 것인지를 선언
            SensorManager.SENSOR_DELAY_NORMAL, // 얼마나 빠른 주기로 센서 값을 읽어올 것인지 선언
        )
    }

    fun unregisterSensor() {
        sensorManager.unregisterListener(this) // 화면이 안보일 때는 센서 동작을 안한다
    }


    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_RESUME) {
            registerSensor()
        } else if (event == Lifecycle.Event.ON_PAUSE) {
            unregisterSensor()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            _x.value = event.values[0]
            _y.value = event.values[1]
        }
    }

    // 센서 딜레이, 감도가 변경될 때 사용되는 기능
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
} // MainViewModel class end

@Composable
fun TiltScreen(x: Float, y: Float) {
    val yCoord = x * 20
    val xCoord = y * 20

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2 // 'Canvas' 가 제공하는 사이즈의 가로 중간 값
        val centerY = size.height / 2 // 'Canvas' 가 제공하는 사이즈의 세로 중간 값

        // 바깥 원
        drawCircle(
            color = Color.Black,
            radius = 100f,
            center = Offset(centerX, centerY), // 중간 점
            style = Stroke(), // 선이냐 안의 색을 꽉채울거냐 선언
        )
        // 녹색 원
        drawCircle(
            color = Color.Green,
            radius = 100f,
            center = Offset(xCoord + centerX, yCoord + centerY), // 중간 점
        )

        // 가운데 십자가 하나의 라인
        drawLine(
            color = Color.Black,
            start = Offset(centerX - 20, centerY), // 시작 점
            end = Offset(centerX + 20, centerY), // 끝 점
        )
        // 가운데 십자가 하나의 라인
        drawLine(
            color = Color.Black,
            start = Offset(centerX, centerY - 20), // 시작 점
            end = Offset(centerX, centerY + 20), // 끝 점
        )

    }
}

@Preview(showBackground = true)
@Composable
fun PreView() {
    TiltScreen(x = 30f, y = 20f)
}