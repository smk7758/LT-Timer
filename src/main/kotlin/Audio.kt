import java.awt.Toolkit
import java.io.Closeable
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths
import javax.sound.sampled.*

class Audio: Closeable, AutoCloseable {
//ref: https://stackoverflow.com/questions/3780406/how-to-play-a-sound-alert-in-a-java-application
//getClass().getClassLoader().getResource("sample.txt");
//getResourceAsStream
//val audio: Clip = createClip(Paths.get("bell.wav"))
    val audio: Clip? = null

    fun createClip(fileURL: URL): Clip? {
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
//        println("path: ${Audio::class.java.getResource("/MainKt.class")}")
        val audioURL = Audio::class.java.getResource("/bell.wav")
        val audioURI = audioURL?.toURI()

        val audio = createClip(audioURL)

//        val audioFile = File(audioURI)
//        println("audio path URI: ${audioURI}")

//        if (audioURI == null) return
//        val audioPath = Paths.get(audioURI)
//        println("audio path: ${audioPath.toAbsolutePath()}")
//        val audio = createClip(audioFile.toPath())

        if (audio != null) {
            audio.start()
            if (twice) {
                Thread.sleep(300)
                audio.start()
            }
        } else {
            Toolkit.getDefaultToolkit().beep() // sound
        }
    }

    override fun close() {
        audio?.close()
    }
}