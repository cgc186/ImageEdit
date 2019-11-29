package first.histogram

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object HistogramDao {
    private val histogram = Histogram()

    fun histogramEdit(filePath: String): BufferedImage {
        return histogram.histogram(ImageIO.read(File(filePath)))
    }
}