package com.plcoding.coroutinesmasterclass.util

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.plcoding.coroutinesmasterclass.ui.theme.CoroutinesMasterclassTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun RotatingBoxScreen(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val angleRatio by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000)
        ),
        label = ""
    )
    var photoUri: Uri? by remember { mutableStateOf(null) }
    var dominantColor = remember { mutableStateOf(Color.White) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        photoUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(dominantColor.value),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LoadImageAndFindDominantColor(imageUri = photoUri, dominantColor)
        Box(
            modifier = Modifier
                .size(100.dp)
                .graphicsLayer {
                    rotationZ = 360f * angleRatio
                }
                .background(Color.Red)

        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = {
                //On button press, launch the photo picker
                launcher.launch(
                    PickVisualMediaRequest(
                    //Here we request only photos. Change this to .ImageAndVideo if
                    //you want videos too.
                    //Or use .VideoOnly if you only want videos.
                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                )
                )
            }
        ) {
            Text("Select Photo")
        }
    }
}

@Composable
fun LoadImageAndFindDominantColor(imageUri: Uri?, dominantColor: MutableState<Color>) {
    val context = LocalContext.current

    LaunchedEffect(imageUri) {
        if(imageUri != null) {
            withContext(Dispatchers.IO) {
                val loader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(imageUri)
                    .allowHardware(false) // Disable hardware bitmaps
                    .build()

                val result = (loader.execute(request) as SuccessResult).drawable

                withContext(Dispatchers.Default) {
                    val bitmap = result.toBitmap()

                    Palette.from(bitmap).generate { palette ->
                        dominantColor.value = Color(palette?.getDominantColor(0) ?: 0)
                    }
                }
            }
        }
    }



// Możesz teraz użyć dominantColor w swojej kompozycji
}
@Preview
@Composable
private fun RotatingBoxScreenPreview() {
    CoroutinesMasterclassTheme {
        RotatingBoxScreen()
    }
    
}