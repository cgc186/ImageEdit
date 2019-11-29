package opencv.bow

import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import java.awt.AlphaComposite
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

object Mat2BufImg {

    /**
     * Mat转换成BufferedImage
     *
     * @param matrix
     * 要转换的Mat
     * @param fileExtension
     * 格式为 ".jpg", ".png", etc
     * @return
     */
    fun Mat2BufImg(matrix: Mat, fileExtension: String): BufferedImage? {
        // convert the matrix into a matrix of bytes appropriate for
        // this file extension
        val mob = MatOfByte()
        Imgcodecs.imencode(fileExtension, matrix, mob)
        // convert the "matrix of bytes" into a byte array
        val byteArray = mob.toArray()
        var bufImage: BufferedImage? = null
        try {
            val `in` = ByteArrayInputStream(byteArray)
            bufImage = ImageIO.read(`in`)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return bufImage
    }

    /**
     * BufferedImage转换成Mat
     *
     * @param original
     * 要转换的BufferedImage
     * @param imgType
     * bufferedImage的类型 如 BufferedImage.TYPE_3BYTE_BGR
     * @param matType
     * 转换成mat的type 如 CvType.CV_8UC3
     */
    fun BufImg2Mat(original: BufferedImage?, imgType: Int, matType: Int): Mat {
        requireNotNull(original) { "original == null" }

        // Don't convert if it already has correct type
        if (original.type != imgType) {

            // Create a buffered image
            val image = BufferedImage(original.width, original.height, imgType)

            // Draw the image onto the new buffer
            val g = image.createGraphics()
            try {
                g.composite = AlphaComposite.Src
                g.drawImage(original, 0, 0, null)
            } finally {
                g.dispose()
            }
        }

        val pixels = (original.raster.dataBuffer as DataBufferByte).data
        val mat = Mat.eye(original.height, original.width, matType)
        mat.put(0, 0, pixels)
        return mat
    }
}