// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.time.LocalDateTime

val myTimer = MyTimer(1)

@Composable
@Preview
fun app() {
    val timerStartButtonEnabled = remember { mutableStateOf(!myTimer.started) }
    val timerStopButtonEnabled = remember { mutableStateOf(myTimer.started) }
    val timerResetButtonEnabled = remember { mutableStateOf(!myTimer.started) }
    val durationText = remember { mutableStateOf(myTimer.durationText) } // Min
    var endAt: LocalDateTime? = null

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text("${myTimer.defaultMinute}分LT タイマー", fontSize = 30.sp)

                // StartButton
                Button(onClick = {
                    if (!myTimer.started) {
                        myTimer.start {
                            // task
                            myTimer.task_(this) {
                                // myTimerで定義されているtaskとupdateする内容を合成する！
                                durationText.value = myTimer.durationText
                            }
                        }
                    }
                    timerStartButtonEnabled.value = false
                    timerStopButtonEnabled.value = true
                    timerResetButtonEnabled.value = false
                    durationText.value = myTimer.durationText
                    endAt = LocalDateTime.now().plusSeconds(myTimer.duration.toSeconds())

                    println("button: $myTimer")
                }, enabled = timerStartButtonEnabled.value) {
                    Text("Start")
                }

                // StopButton
                Button(onClick = {
                    if (myTimer.started) {
                        myTimer.stop()
                        timerStartButtonEnabled.value = true
                        timerStopButtonEnabled.value = false
                        timerResetButtonEnabled.value = true
                        durationText.value = myTimer.durationText
                    }
                }, enabled = timerStopButtonEnabled.value) {
                    Text("Stop")
                }

                // ResetButton
                Button(onClick = {
                    myTimer.reset()
                    durationText.value = myTimer.durationText
                }, enabled = timerResetButtonEnabled.value) {
                    Text("Reset")
                }
            }
            Text(durationText.value)
            Text("Now: ${LocalDateTime.now()}")
            Text("End at: ${if (endAt != null) endAt.toString() else "--" }")
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        app()
    }
}

