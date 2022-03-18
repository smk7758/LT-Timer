// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

val myTimer = MyTimer(5)

@Composable
@Preview
fun app() {
    val timerButtonText = remember { mutableStateOf(myTimer.timerButtonText_) }
    val durationText = remember { mutableStateOf(myTimer.durationText_) } // Min

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text("5分LT タイマー", fontSize = 30.sp)
                Button(onClick = {
                    if (!myTimer.started) {
                        myTimer.start()
                    } else {
                        myTimer.stop()
                    }

                    timerButtonText.value = myTimer.timerButtonText_

                    println("button: $myTimer")
                }) {
                    Text(timerButtonText.value)
                }
//                Button(onClick = {
//
//                }) {
//                    Text("Stop")
//                }
                Button(onClick = {

                }) {
                    Text("Reset")
                }
            }
            Text(durationText.value)
            Text("End in: ${LocalDateTime.now()}")
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        app()
    }
}

