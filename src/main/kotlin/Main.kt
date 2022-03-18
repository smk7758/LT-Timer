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

    val timer = Timer()
    val task: TimerTask.() -> Unit = {
        myTimer.duration = myTimer.duration.minusSeconds(1)
        println("minus: ${myTimer.durationText_}")
        if (myTimer.duration.toSeconds() <= 0) {
            println("timer finish!")
            myTimer.reset()
            this.cancel()
        }
    }

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
                    myTimer.started = !myTimer.started
                    timerButtonText.value = myTimer.timerButtonText_
                    println("button: $myTimer")

                    timer.scheduleAtFixedRate(delay = 0, period = 1000, task) // 1s
                }) {
                    Text(timerButtonText.value)
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

