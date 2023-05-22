package com.example.app_gpsmap

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 위치 정보 권한 승인
            // 'remember' 사용시 import 문의 getValue, setValue 꼭 확인
            var granted by remember { mutableStateOf(false) }

            // 위치 권한을 요청하는 객체
            // 권한을 요청할 때는 'ActivityResultContracts.RequestPermission()' 객체를 사용한다
            val launcher =
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        granted = isGranted
                    }
                )

            // 권한이 있는지 확인
            // 'Manifest' 사용시 import 문을 android 레벨로 맞추기
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                granted = true
            }

            if (granted) {
                val viewModel = viewModel<MainViewModel>()
                lifecycle.addObserver(viewModel) // 'Activity' 레벨의 생명주기와 'MainViewModel' 에서 선언한 생명주기를 연결한다
                MyMap(viewModel = viewModel)
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = "권한이 허용되지 않았습니다.")
                    Button(onClick = {
                        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION) // 'ACCESS_FINE_LOCATION' 하나만 허용해도 'ACCESS_COARSE_LOCATION' 같이 허용된다
                    }) {
                        Text(text = "권한 요청.")
                    }
                }
            }
        }
    }
}

class MainViewModel(application: Application) : AndroidViewModel(application),
    LifecycleEventObserver {
    private val fusedLPC: FusedLocationProviderClient
    private val locationRequest: LocationRequest
    private val locationCallback: MyLocationCallBack

    /* 위치 값을 두번 가져오는 형태
    private val _location = mutableStateOf<Location?>(null)
    val location: State<Location?> = _location
    private val _polylineOptions = mutableStateOf(PolylineOptions().width(5f).color(Color.RED))
    val polylineOptions: State<PolylineOptions> = _polylineOptions*/

    // 위치 값을 한번만 가져오는 형태
    private val _state = mutableStateOf(MapState(null, PolylineOptions().width(5f).color(Color.RED)))
    val state: State<MapState> = _state

    init {
        fusedLPC = LocationServices.getFusedLocationProviderClient(application.applicationContext)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build()

        locationCallback = MyLocationCallBack()
    }

    @SuppressLint("MissingPermission") // 'permission' 체크를 해야 하지만 여기서는 무시한다
    private fun addLocationListener() {
        fusedLPC.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun removeLocationListener() {
        fusedLPC.removeLocationUpdates(locationCallback)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_RESUME) {
            addLocationListener()
        } else if (event == Lifecycle.Event.ON_PAUSE) {
            removeLocationListener()
        }
    }

    inner class MyLocationCallBack : LocationCallback() {
        // 'lcation' 얻게 되었을 때 호출되는 함수
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            val location = locationResult.lastLocation // 현재 위치
            val polylineOptions = state.value.polylineOptions

            // 'viewModel' 에서 만들어진 위치정보를 외부 UI 의 전달하기 위한 세팅
            _state.value = state.value.copy(
                location = location,
                polylineOptions = polylineOptions.add(location?.let { LatLng(it.latitude, location.longitude) })
            )
            /*_location.value = location
            _polylineOptions.value =
                polylineOptions.value.add(location?.let { LatLng(it.latitude, location.longitude) })*/
        }
    }


}

data class MapState(
    val location: Location?,
    val polylineOptions: PolylineOptions,
)

@Composable
fun MyMap(
    viewModel: MainViewModel
) {
    val map = rememberMapView()
    val state = viewModel.state.value
   /* val location = viewModel.location.value
    val polylineOptions = viewModel.polylineOptions.value*/


    AndroidView(
        factory = { map },
        update = { mapView ->
            mapView.getMapAsync() { googleMap ->
                // 'location' 정보를 안전하게 받아서 실행하기 위한 작업
                state.location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))

                    googleMap.addPolyline(state.polylineOptions) // 위치정보를 가져와 'line' 을 그린다
                }

                // 'Map' 이 호출되면 초기화면은 시드니에 마커를 찍고 이동하는 로직
                /*val sydney = LatLng(-34.0, 151.0)
                googleMap.addMarker(
                    MarkerOptions().position(sydney).title("Marker in Sydney")
                )
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/
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