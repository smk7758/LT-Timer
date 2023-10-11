import java.awt.Toolkit
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.nio.file.Paths
import javax.sound.sampled.*
import kotlin.math.log10


class Audio {
//    private val audioBellURL = getAudioResource("/bell.wav")
//    private val audioStartSoundURL = getAudioResource("/start.wav")
private val audioBellURL = Paths.get("C:/Programming/InteliJ/LT-Timer/src/main/kotlin/resouces/bell.wav").toUri().toURL()
private val audioStartSoundURL = Paths.get("C:/Programming/InteliJ/LT-Timer/src/main/kotlin/resouces/start.wav").toUri().toURL()

    private fun getAudioResource(fileName: String): URL {
        println(Audio::class.java.getResource("")?.path.toString())

        return Audio::class.java.getResource(fileName)
            ?:  error("Cannot find the file. $fileName")
    }

    private fun createClip(fileURL: URL): Clip? {
        //ref: https://nompor.com/2017/12/14/post-128/
        try {
            AudioSystem.getAudioInputStream(fileURL).use { ais ->
                //ファイルの形式取得
                val af: AudioFormat = ais.format

                //単一のオーディオ形式を含む指定した情報からデータラインの情報オブジェクトを構築
                val dataLine = DataLine.Info(Clip::class.java, af)

                //指定された Line.Info オブジェクトの記述に一致するラインを取得
                val clip = AudioSystem.getLine(dataLine) as Clip

                //再生準備完了
                clip.open(ais)

                return clip
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: UnsupportedAudioFileException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: LineUnavailableException) {
            e.printStackTrace()
        }

        return null
    }

    fun playRing(twice: Boolean = false, volume: Int = 100) {
        val runnable = Runnable {
            val audio = createClip(audioBellURL)
            println("play: $audioBellURL")
            if (audio != null) {
                val ctrl = audio.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
                ctrl.value = (volume / 100).toFloat()

                audio.start()
            } else {
                Toolkit.getDefaultToolkit().beep() // sound
            }

            if (twice) {
                Thread.sleep(200)
                playRing()
            }
            Thread.sleep(500)
        }

        Thread(runnable).start()
    }

    fun playStartSound() {
        playSound(audioStartSoundURL, volume = 50)
    }

    fun playSound(audioURL: URL, reverbMillis: Long = 500, volume: Int = 100) {
        val runnable = Runnable {
            val audio = createClip(audioURL)

            if (audio != null) {
                val ctrl = audio.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
                ctrl.value = log10((volume.toFloat() / 100).toDouble()).toFloat() * 20 // dB
                audio.start()
            } else {
                Toolkit.getDefaultToolkit().beep() // sound
            }

            Thread.sleep(reverbMillis)
        }

        Thread(runnable).start()
    }
}