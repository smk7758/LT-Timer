// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.time.Duration
import java.time.LocalDateTime
import java.io.IOException
import java.awt.Toolkit
import javax.sound.sampled.*
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths

val audio = Audio()

@Composable
@Preview
@ExperimentalMaterialApi
fun app() {
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val myTimer1 = MyTimer(1, 1)
            ltTimer(myTimer1)
            val myTimer5 = MyTimer(5, 4)
            ltTimer(myTimer5)
            val myTimer15 = MyTimer(15, 12)
            ltTimer(myTimer15)
        }
    }
}

@Composable
@ExperimentalMaterialApi
fun ltTimer(myTimer: MyTimer) {
    val timerStartButtonEnabled = remember { mutableStateOf(!myTimer.started) }
    val timerStopButtonEnabled = remember { mutableStateOf(myTimer.started) }
    val timerResetButtonEnabled = remember { mutableStateOf(!myTimer.started) }
    val durationText = remember { mutableStateOf(myTimer.durationText) } // Min
    val openDialog = remember { mutableStateOf(false) }

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
                        myTimer.task_(this, {
                            // myTimerで定義されているtaskとupdateする内容を合成する！
                            durationText.value = myTimer.durationText

                            if (myTimer.duration.minus(Duration.ofMinutes(myTimer.firstRing.toLong())).isZero) {
                                audio.playRing()
//                                Toolkit.getDefaultToolkit().beep() // sound
                            }
                        }, {
                            audio.playRing(true)
                            Toolkit.getDefaultToolkit().beep() // sound
                            Toolkit.getDefaultToolkit().beep() // sound

                            openDialog.value = true
                        })
                    }
                }
                timerStartButtonEnabled.value = false
                timerStopButtonEnabled.value = true
                timerResetButtonEnabled.value = false
                durationText.value = myTimer.durationText
                myTimer.endAt = LocalDateTime.now().plusSeconds(myTimer.duration.toSeconds())

                println("button: $myTimer, endAt: $myTimer.endAt")
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

                openDialog.value = true // TODO
                audio.playRing()
            }, enabled = timerResetButtonEnabled.value) {
                Text("Reset")
            }
        }
        Text(durationText.value)
        Text("Now: ${LocalDateTime.now()}")
        Text(
            "End at: ${
                if (myTimer.endAt != null) myTimer.endAt.toString() else {
                    println("??: $myTimer.endAt"); "--"
                }
            }"
        )
        alertDialog(openDialog, myTimer)
    }
}

@ExperimentalMaterialApi
@Composable
// ref: https://techbooster.org/android/ui/18505/
fun alertDialog(openDialog: MutableState<Boolean>, myTimer: MyTimer) {
    MaterialTheme {
        Column {
            Button(onClick = {
                openDialog.value = true
            }) {
                Text("Click me")
            }

            if (openDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        // Dismiss the dialog when the user clicks outside the dialog or on the back
                        // button. If you want to disable that functionality, simply use an empty
                        // onCloseRequest.
//                        openDialog.value = false
                    },
                    title = {
                        Text("LT-Timer")
                    },
                    text = {
                        Text("${myTimer.defaultMinute}分のLT終了！")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                openDialog.value = false
                            }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}

@ExperimentalMaterialApi
fun main() = application {
    Window(onCloseRequest = {
        audio.close()
        exitApplication()
    }) {
        app()
    }
}


