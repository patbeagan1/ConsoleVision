package dev.patbeagan.consolevision.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.isActive

@Composable
fun rememberFrameRate(): State<Int> {
    val frameRate = remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        var frameCount = 0
        var prevTime = withFrameNanos { it }

        while (isActive) {
            withFrameNanos {
                frameCount++

                val seconds = (it - prevTime) / 1E9 // 1E9 nanoseconds is 1 second
                if (seconds >= 1) {
                    frameRate.value = ((frameCount / seconds).toInt())
                    prevTime = it
                    frameCount = 0
                }
            }
        }
    }
    return frameRate
}
