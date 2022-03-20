// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter




val audio = Audio()

@Composable
@Preview
@ExperimentalMaterialApi
fun app() {
    MaterialTheme(colors = lightColors(primary = Color(82, 112, 196))) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().fillMaxHeight().fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val myTimer1 = MyTimer(1, 1)
            myTimer1.duration = Duration.ofSeconds(1) // 1s
            ltTimer(myTimer1, true)
            val myTimer5 = MyTimer(5, 1)
            ltTimer(myTimer5)
            val myTimer15 = MyTimer(15, 3)
            ltTimer(myTimer15)

            Column(modifier = Modifier.fillMaxWidth().background(Color.Gray)) {
                Row (Modifier.align(alignment = Alignment.End)) {
                    // Play Ring Button
                    Button(modifier = Modifier.background(Color.Green).padding(vertical = 5.dp, horizontal = 5.dp), onClick = {
                        audio.playRing()
                    }) {
                        Text("Ring")
                    }

                    // Play Ring Twice Button
                    Button(modifier = Modifier.background(Color.Green).padding(vertical = 5.dp, horizontal = 5.dp), onClick = {
                        audio.playRing(true)
                    }) {
                        Text("Ring Twice")
                    }
                }
            }
        }
    }
}

@Composable
@ExperimentalMaterialApi
fun ltTimer(myTimer: MyTimer, isTop: Boolean = false) {
    val timerStartButtonEnabled = remember { mutableStateOf(!myTimer.started) }
    val timerStopButtonEnabled = remember { mutableStateOf(myTimer.started) }
    val timerResetButtonEnabled = remember { mutableStateOf(!myTimer.started) }
    val durationText = remember { mutableStateOf(myTimer.durationText) } // Min
    val openDialog = remember { mutableStateOf(false) }

    val update = {
        // myTimerで定義されているtaskとupdateする内容を合成する！
        durationText.value = myTimer.durationText

        if (myTimer.duration.minus(Duration.ofMinutes(myTimer.firstRing.toLong())).isZero) {
            audio.playRing()
//                                Toolkit.getDefaultToolkit().beep() // sound
        }
    }
    val onFinished = {
        audio.playRing(true)
//        Toolkit.getDefaultToolkit().beep() // sound
//        Toolkit.getDefaultToolkit().beep() // sound

        timerStartButtonEnabled.value = !myTimer.started
        timerStopButtonEnabled.value = myTimer.started
        timerResetButtonEnabled.value = !myTimer.started

        openDialog.value = true
    }

    var modifier = if (!isTop) Modifier.padding(top = 8.dp) else Modifier.padding(all = 0.dp)
    modifier = modifier.background(color = Color(240, 240, 240)).padding(all = 5.dp).fillMaxWidth()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text("%2s分LT タイマー".format(myTimer.defaultMinute.toString()), fontSize = 35.sp, fontWeight = FontWeight.Medium)

            // StartButton
            Button(modifier = Modifier.padding(horizontal = 5.dp),
                onClick = {
                    if (!myTimer.started) {
                        myTimer.start {
                            // task
                            myTimer.task(this, update, onFinished)
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
            Button(modifier = Modifier.padding(horizontal = 5.dp),
                onClick = {
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
            Button(modifier = Modifier.padding(horizontal = 5.dp),
                onClick = {
                    myTimer.reset()
                    durationText.value = myTimer.durationText

    //                openDialog.value = true
            }, enabled = timerResetButtonEnabled.value) {
                Text("Reset")
            }
        }

        Text(durationText.value,
            fontSize = 45.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(5.dp))

        Column {
//            Text("Now: ${LocalDateTime.now()}")
            val dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
            Text("End at: ${if (myTimer.endAt != null) myTimer.endAt!!.format(dtf) else "--"}",
                fontSize = 20.sp
            )
        }
        alertDialog(openDialog, myTimer, update)
    }
}

@ExperimentalMaterialApi
@Composable
// ref: https://techbooster.org/android/ui/18505/
fun alertDialog(openDialog: MutableState<Boolean>, myTimer: MyTimer, update: () -> Unit = {}) {
    if (!openDialog.value) return

    AlertDialog(modifier = Modifier.size(200.dp, 150.dp),
    onDismissRequest = {
        // Dismiss the dialog when the user clicks outside the dialog or on the back
        // button. If you want to disable that functionality, simply use an empty
        // onCloseRequest.
                openDialog.value = false
    },
    title = {
        Text("LT-Timer")
    },
    text = {
        Text("%2s分のLT終了！".format(myTimer.defaultMinute.toString()), fontSize = 20.sp, modifier = Modifier.fillMaxWidth())
    },
    confirmButton = {
        Button(
            onClick = {
                openDialog.value = false
                update()
            }) {
            Text("OK")
            }
        }
    )
}

@ExperimentalMaterialApi
fun main() = application {
    Window(state = WindowState(size = DpSize(550.dp, 700.dp)), onCloseRequest = {
        exitApplication()
    }) {
        app()
    }
}


