import java.awt.Toolkit
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import javax.sound.sampled.*

class Audio {
    val audioBellURL = Audio::class.java.getResource("/bell.wav")
    val audioStartSoundURL = Audio::class.java.getResource("/\"魔王魂 効果音 システム40.wav\"")

    fun createClip(fileURL: URL): Clip? {
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

                return clip;
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

    fun playRing(twice: Boolean = false) {
        val runnable = Runnable {
            val audio = createClip(audioBellURL);
            println("play: $audioBellURL")
            if (audio != null) {
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
        playSound(audioStartSoundURL)
    }

    fun playSound(audioURL: URL, reverbMillis: Long = 500) {
        val runnable = Runnable {
            val audio = createClip(audioURL)

            if (audio != null) {
                audio.start()
            } else {
                Toolkit.getDefaultToolkit().beep() // sound
            }

            Thread.sleep(reverbMillis)
        }

        Thread(runnable).start()
    }
}