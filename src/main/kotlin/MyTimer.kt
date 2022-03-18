import java.time.Duration

class MyTimer(val defaultMinute: Long = 5) {
    var started: Boolean = false
    val timerButtonText_: String
        get() = if (!started) "Start" else "Stop"
    var duration = Duration.ofMinutes(defaultMinute)
    val durationText_: String
        get() = "${duration.toMinutes()} : ${duration.toSecondsPart()}"

    fun reset(minute: Long = defaultMinute) {
        duration = Duration.ofMinutes(minute)
    }

    override fun toString(): String {
        return "started: ${started}, text: ${timerButtonText_}"
    }
}