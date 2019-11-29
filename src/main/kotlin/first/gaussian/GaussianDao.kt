package first.gaussian

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object GaussianDao {
    private val gaussian: Gaussian = Gaussian()
    fun gaussianEdit(filePath: String): BufferedImage {
        val img = ImageIO.read(File(filePath))
        return gaussian.myGaussianFilter(img, 3, 1.5f)
    }
}