package com.example.app_gpsmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyMap()
        }
    }
}

@Composable
fun MyMap() {
    val map = rememberMapView()

    AndroidView(
        factory = { map },
        update = { mapView ->
            mapView.getMapAsync() { googleMap ->
                // 'Map' 이 호출되면 초기화면은 시드니에 마커를 찍고 이동하는 로직
                val sydney = LatLng(-34.0, 151.0)
                googleMap.addMarker(
                    MarkerOptions().position(sydney).title("Marker in Sydney")
                )
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
            } // 'Map' 이 준비되면 호출되는 함수
        }
    )
}

@Composable
fun rememberMapView(): MapView {
    val context = LocalContext.current // 컴포저블 안에서 로컬 'context' 얻는 방법
    val mapView = remember {
        MapView(context)
    }

    // 'MainActivity' 에서의 생명주기와 'MapView' 의 생명주기는 다르므로 서로 동기화 될 수 있게 제어 해주는 로직이 필요하다.
    // 'lifecycleOwner' 는 'MainActivity' 의 생명주기이다.
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle()) // 빈 'Bundle()' 을 넣어준다.
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                //Lifecycle.Event.ON_ANY -> mapView 에는 없는 생명주기이다.
                else -> throw IllegalStateException()
            }
        } // LifecycleEventObserver function end

        lifecycleOwner.lifecycle.addObserver(observer) // mapView 의 생명주기를 감지해서 동작하는 설정

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer) // mapView 의 생명주기를 감지하는 동작 해지 설정
        }
    } // DisposableEffect function end

    return mapView
}