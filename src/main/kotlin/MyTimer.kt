import java.time.Duration
import java.util.*

class MyTimer(val defaultMinute: Long = 5) {
    var started: Boolean = false
    val timerButtonText_: String
        get() = if (!started) "Start" else "Stop"
    var duration = Duration.ofMinutes(defaultMinute)
    val durationText_: String
        get() = "${duration.toMinutes()} : ${duration.toSecondsPart()}"
    private val timer = Timer()
    val task: TimerTask.() -> Unit = {
        myTimer.duration = myTimer.duration.minusSeconds(1)
//        durationText.value = myTimer.durationText_
        println("minus: ${myTimer.durationText_}")

        setDurationText(myTimer.durationText_)

        if (myTimer.duration.toSeconds() <= 0) {
            this.cancel()
            println("timer finish!")
            myTimer.reset()
        }
    }

    fun start(minute: Long = defaultMinute) {
        started = true
        duration = Duration.ofMinutes(minute)
        timer.scheduleAtFixedRate(delay = 0, period = 1000, task) // 1s
    }

    fun stop() {
        started = false
        timer.cancel()
    }

    fun reset(minute: Long = defaultMinute) {
        stop()
        duration = Duration.ofMinutes(minute)
    }

    override fun toString(): String {
        return "started: $started, text: $timerButtonText_, duration: $durationText_"
    }
}