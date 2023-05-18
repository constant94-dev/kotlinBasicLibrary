package com.example.app_electronicgallery

import android.Manifest
import android.app.Application
import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize.Fill.calculateMainAxisPageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.*
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel = viewModel<MainViewModel>()
            var granted by remember { mutableStateOf(false) }


            // 권한 요청에 대한 클래스는 제공된다. 'ActivityResultContracts.RequestPermission()'
            val launcher =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
                    granted = isGranted
                }
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                granted = true
            }

            if (granted) {
                viewModel.fetchPhotos()
                HomeScreen(photoUris = viewModel.photoUris.value)
            } else {
                PermissionRequestScreen {
                    launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        } // setContent end
    }
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _photoUris = mutableStateOf(emptyList<Uri>())
    val photoUris: State<List<Uri>> = _photoUris
    fun fetchPhotos() {
        val uris = mutableListOf<Uri>()

        getApplication<Application>().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            "${MediaStore.Images.ImageColumns.DATE_TAKEN} DESC"
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id,
                )
                uris.add(contentUri)
            }
        }
    }
}

@Composable
fun PermissionRequestScreen(onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "권한이 허용되지 않았습니다.")
        Button(onClick = onClick) {
            Text(text = "권한 요청.")
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun HomeScreen(photoUris: List<Uri>) {
    val pagerState = rememberPagerState()

    Column(Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            pageCount = photoUris.size,
            modifier = Modifier
                .weight(1f)
                .padding(
                    16.dp
                )
                .fillMaxSize(),
        ) { pageIndex ->
            Card(
                modifier = Modifier
                    .graphicsLayer {
                        val pageOffset = pagerState.currentPageOffsetFraction

                        lerp(
                            start = 0.85f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f),
                        ).also { scale ->
                            scaleX = scale
                            scaleY = scale
                        }

                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f),
                        )
                    }
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(photoUris[pageIndex])
                            .size(Size.ORIGINAL)
                            .build()
                    ),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            pageCount = photoUris.size,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
        )
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float =
    (1 - fraction) * start + fraction * stop