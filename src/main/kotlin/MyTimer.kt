import java.time.Duration
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class MyTimer(val defaultMinute: Long = 5) {
    var started: Boolean = false
    var duration = Duration.ofMinutes(defaultMinute)
    val durationText_: String
        get() = "${duration.toMinutes()} : ${duration.toSecondsPart()}"
    val task_ = { task: TimerTask, update: () -> Unit ->
        myTimer.duration = myTimer.duration.minusSeconds(1)
        println("minus: ${myTimer.durationText_}")

        update()

        if (myTimer.duration.toSeconds() <= 0) {
            task.cancel()
            println("timer finish!")
            myTimer.reset()
        }
    }
    private var timer = Timer(true)

    fun start(task: TimerTask.() -> Unit, minute: Long = defaultMinute) {
        started = true
        duration = Duration.ofMinutes(minute)
        timer.scheduleAtFixedRate(delay = 0, period = 1000, task) // 1s
    }

    fun stop() {
        started = false
        timer.cancel()
        timer = Timer(true)
    }

    fun reset(minute: Long = defaultMinute) {
        stop()
        duration = Duration.ofMinutes(minute)
    }

    override fun toString(): String {
        return "started: $started, duration: $durationText_"
    }
}