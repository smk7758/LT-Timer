import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class MyTimer(val defaultMinute: Long = 5, val firstRing: Int = 1) {
    var started: Boolean = false
    var duration = Duration.ofMinutes(defaultMinute)
    val durationText: String
        get() = String.format("%d : %02d ↓", duration.toMinutes(), if (duration.toSeconds() >= 0) duration.toSecondsPart() else 0)
    var endAt: LocalDateTime? = null
    val task_ = { task: TimerTask, update: () -> Unit, finished: () -> Unit ->
        duration = duration.minusSeconds(1)
        println("minus: ${durationText}")

        update()

        if (duration.toSeconds() < 0) {
            task.cancel()
            println("timer finish!")
            reset()
            finished()
        }
    }
    private var timer = Timer(true)

    fun start(task: TimerTask.() -> Unit) {
        started = true
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
        return "started: $started, duration: $durationText"
    }
}
