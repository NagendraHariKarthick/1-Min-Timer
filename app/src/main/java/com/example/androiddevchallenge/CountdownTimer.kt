/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.Margin
import com.example.androiddevchallenge.ui.theme.MyTheme
import java.lang.Integer.parseInt
import kotlin.math.min

@Composable
fun CountdownTimer() {
    val context = LocalContext.current

    val millisInFuture = DateUtils.MINUTE_IN_MILLIS - DateUtils.SECOND_IN_MILLIS
    val countdownInterval = DateUtils.SECOND_IN_MILLIS

    var millisUntilBeFinished by rememberSaveable { mutableStateOf(0L) }
    val initialTime = millisInFuture.formatElapsedTime()
    var time by remember { mutableStateOf(initialTime) }
    var hasStarted by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(hasStarted) {
        val countdownTimer = object : CountDownTimer(
            if (millisUntilBeFinished == 0L) millisInFuture else millisUntilBeFinished,
            countdownInterval
        ) {

            override fun onTick(millisUntilFinished: Long) {
                millisUntilBeFinished = millisUntilFinished
                time = millisUntilFinished.formatElapsedTime()
            }

            override fun onFinish() {
                hasStarted = false
                millisUntilBeFinished = 0
                time = context.getString(R.string.countdown_timer_done_text)
            }
        }
        if (hasStarted) {
            countdownTimer.start()
        } else {
            countdownTimer.cancel()
        }
        onDispose {
            countdownTimer.cancel()
        }
    }

    CountdownTimerScreen(
        time,
        hasStarted,
        onStartClick = { hasStarted = true },
        onStopClick = { hasStarted = false },
        onResetClick = {
            hasStarted = false
            millisUntilBeFinished = 0
            time = initialTime
        }
    )
}

@Composable
private fun CountdownTimerScreen(
    time: String,
    hasStarted: Boolean,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onResetClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            Modifier.padding(16.dp)
        )
        Canvas(
            Modifier
                .height(300.dp)
                .align(Alignment.Center)
                .fillMaxWidth()
        ) {
            val clockRadius = 0.95f * min((size.width / 2), (size.height / 2))
            var style: DrawStyle
            val centerX = (size.width / 2)
            val centerY = (size.height / 2)
            val oneMinuteRadians = Math.PI / 30

            val cap: Int = try {
                val num = parseInt(time.substring(3, 5))
                60 - num
            } catch (e: NumberFormatException) {
                0
            }

            1.rangeTo(60).forEach { minute ->
                val isHour = minute % 5 == 0
                val degree = -Math.PI / 2 + (minute * oneMinuteRadians)
                val x = centerX + kotlin.math.cos(degree) * clockRadius
                val y = centerY + kotlin.math.sin(degree) * clockRadius

                val radius: Float
                if (isHour) {
                    style = Fill
                    radius = 12f
                } else {
                    style = Stroke()
                    radius = 6f
                }
                drawCircle(
                    if (cap > minute) Color.Red else Color.Green,
                    radius,
                    center = (Offset(x.toFloat(), y.toFloat())),
                    style = style,
                )
            }
        }
        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(
                text = time,
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.h2.copy(fontWeight = FontWeight.Normal)
            )
            TextButton(
                onClick = onResetClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(stringResource(R.string.countdown_timer_reset_button))
            }
        }
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = Margin.default, vertical = Margin.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Spacer(modifier = Modifier.weight(.6f))
            ControlButton(hasStarted, onStartClick, onStopClick)
            Spacer(modifier = Modifier.weight(.1f))
        }
    }
}

@Composable
private fun ControlButton(
    hasStarted: Boolean,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit
) {
    FloatingActionButton(
        backgroundColor = if (hasStarted) {
            MaterialTheme.colors.onSecondary
        } else {
            MaterialTheme.colors.secondary
        },
        contentColor = if (hasStarted) {
            MaterialTheme.colors.secondary
        } else {
            MaterialTheme.colors.onSecondary
        },
        onClick = {
            if (hasStarted) {
                onStopClick()
            } else {
                onStartClick()
            }
        }
    ) {
        if (hasStarted) {
            Icon(
                imageVector = Icons.Rounded.Stop,
                contentDescription = stringResource(R.string.countdown_timer_stop_button)
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = stringResource(R.string.countdown_timer_start_button)
            )
        }
    }
}

private fun Long.formatElapsedTime() =
    DateUtils.formatElapsedTime(this / DateUtils.SECOND_IN_MILLIS)

@Preview
@Composable
private fun Preview() {
    MyTheme() {
        CountdownTimerScreen("12:34:56", false, {}, {}, {})
    }
}
