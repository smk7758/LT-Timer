import java.awt.Toolkit
import java.io.Closeable
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths
import javax.sound.sampled.*

class Audio {
    val audioURL = Audio::class.java.getResource("/bell.wav")

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
        val audio = createClip(audioURL);
        println("play: $audioURL")
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
}