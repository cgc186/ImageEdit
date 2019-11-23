package second.edgeDetection

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object EdgeDetectionDao {
    private val edgeDetection= EdgeDetection()

    fun sobel1(filePath: String): BufferedImage {
        return edgeDetection.sobel1(ImageIO.read(File(filePath)))
    }

    fun sobel1ByImage(img:BufferedImage): BufferedImage {
        return edgeDetection.sobel1(img)
    }


    fun sobel2(filePath: String): BufferedImage {
        return edgeDetection.sobel2(ImageIO.read(File(filePath)))
    }

    fun sobel2ByImage(img:BufferedImage): BufferedImage {
        return edgeDetection.sobel2(img)
    }

    fun sobel3(filePath: String): BufferedImage {
        return edgeDetection.sobel3(ImageIO.read(File(filePath)))
    }

    fun sobel3ByImage(img:BufferedImage): BufferedImage {
        return edgeDetection.sobel3(img)
    }


}