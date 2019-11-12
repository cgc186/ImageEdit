package lda

import org.opencv.core.Mat

import java.awt.image.BufferedImage

internal object mat2BufferedImage {

    fun matToBufferedImage(matrix: Mat): BufferedImage? {
        val cols = matrix.cols()
        val rows = matrix.rows()
        val elemSize = matrix.elemSize().toInt()
        val data = ByteArray(cols * rows * elemSize)
        val type: Int
        matrix.get(0, 0, data)
        when (matrix.channels()) {
            1 -> type = BufferedImage.TYPE_BYTE_GRAY
            3 -> {
                type = BufferedImage.TYPE_3BYTE_BGR
                var b: Byte
                var i = 0
                while (i < data.size) {
                    b = data[i]
                    data[i] = data[i + 2]
                    data[i + 2] = b
                    i = i + 3
                }
            }
            else -> return null
        }
        val image2 = BufferedImage(cols, rows, type)
        image2.raster.setDataElements(0, 0, cols, rows, data)
        return image2
    }

}