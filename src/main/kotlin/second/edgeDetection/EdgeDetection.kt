package second.edgeDetection

import edge.MyImage
import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import kotlin.math.sqrt

class EdgeDetection {

    private fun toGray(img: BufferedImage): BufferedImage {
        val image = BufferedImage(img.width, img.height, Image.SCALE_DEFAULT)

        var data = img.getRGB(0, 0, img.width, img.height, null, 0, img.width)
        for (y in 0 until img.height) {
            for (x in 0 until img.width) {
                val c = data[x + y * img.width]
                val r = c shr 16 and 0xFF
                val g = c shr 8 and 0xFF
                val b = c shr 0 and 0xFF
                data[x + y * img.width] = (0.21f * r + 0.70f * g + 0.07f * b).toInt() //to gray
            }
        }
        image.setRGB(0, 0, img.width, img.height, data, 0, img.width)
        return image
    }

    private fun getNumPixel(img: BufferedImage): Array<IntArray> {
        var numPixel = Array(img.width) { IntArray(img.height) }
        for (x in 0 until img.width) {
            for (y in 0 until img.height) {
                numPixel[x][y] = img.getRGB(x, y)
            }
        }
        return numPixel
    }

    fun sobel2(img: BufferedImage): BufferedImage {

        val sobelX = arrayOf(
            shortArrayOf(1, 0, -1),
            shortArrayOf(2, 0, -2),
            shortArrayOf(1, 0, -1)
        )

        val sobelY = arrayOf(
            shortArrayOf(1, 2, 1),
            shortArrayOf(0, 0, 0),
            shortArrayOf(-1, -2, -1)
        )
        val size = 3
        val grayImg = toGray(img)

        var w = grayImg.width
        var h = grayImg.height
        val gray = getNumPixel(grayImg)

        val image = BufferedImage(w, h, Image.SCALE_DEFAULT)

        for (x in 0 until w - size + 1) {
            for (y in 0 until h - size + 1) {
                var tempX = 0
                var tempY = 0
                for (i in 0 until size) {
                    for (j in 0 until size) {
                        tempX += gray[x + i][y + j] * sobelX[i][j];
                        tempY += gray[x + i][y + j] * sobelY[i][j];
                    }
                }
                val i1 = (tempX * tempX) + (tempY * tempY)
                var result = sqrt(i1.toDouble()).toInt()
                val GMax = 200

                if (result > GMax) result = 255
                if (result <= GMax) result = 0
                image.setRGB(x, y, Color(result, result, result).rgb)

            }
        }
        return image
    }

    private fun toImage(w: Int, h: Int, data: IntArray): BufferedImage {
        val image = BufferedImage(w, h, Image.SCALE_DEFAULT)
        val d = IntArray(w * h)
        for (i in 0 until h) {
            for (j in 0 until w) {
                d[j + i * w] =
                    255 shl 24 or (data[j + i * w] shl 16) or (data[j + i * w] shl 8) or data[j + i * w]
            }
        }
        image.setRGB(0, 0, w, h, d, 0, w)
        return image
    }

    fun sobel3(img: BufferedImage): BufferedImage? {

        val grayImg = toGray(img)
        var w = grayImg.width
        var h = grayImg.height
        var data = grayImg.getRGB(0, 0, w, h, null, 0, w);

        val d = IntArray(w * h)

        for (j in 1 until h - 1) {
            for (i in 1 until w - 1) {
                val s1 =
                    data[i - 1 + (j + 1) * w] + 2 * data[i + (j + 1) * w] + data[i + 1 + (j + 1) * w] - data[i - 1 + (j - 1) * w] - 2 * data[i + (j - 1) * w] - data[i + 1 + (j - 1) * w]
                val s2 =
                    data[i + 1 + (j - 1) * w] + 2 * data[i + 1 + j * w] + data[i + 1 + (j + 1) * w] - data[i - 1 + (j - 1) * w] - 2 * data[i - 1 + j * w] - data[i - 1 + (j + 1) * w]

                val i1 = (s1 * s1) + (s2 * s2)
                var s = sqrt(i1.toDouble()).toInt()

                if (s < 0)
                    s = 0
                if (s > 255)
                    s = 255
                d[i + j * w] = s
            }
        }
        return toImage(w, h, d)
    }

    fun sobel1(img: BufferedImage): BufferedImage? {

//        val grayImg = ImageUtil.toGray(img)
//        var w = grayImg.width
//        var h = grayImg.height
//        var data = grayImg.getRGB(0, 0, w, h, null, 0, w);
        val image = MyImage(img)
        var data = image.data
        var w = image.w
        var h = image.h
        val d = IntArray(w * h)

        for (j in 1 until h - 1) {
            for (i in 1 until w - 1) {
                val s1 =
                    data[i - 1 + (j + 1) * w] + 2 * data[i + (j + 1) * w] + data[i + 1 + (j + 1) * w] - data[i - 1 + (j - 1) * w] - 2 * data[i + (j - 1) * w] - data[i + 1 + (j - 1) * w]
                val s2 =
                    data[i + 1 + (j - 1) * w] + 2 * data[i + 1 + j * w] + data[i + 1 + (j + 1) * w] - data[i - 1 + (j - 1) * w] - 2 * data[i - 1 + j * w] - data[i - 1 + (j + 1) * w]
                val i1 = (s1 * s1) + (s2 * s2)
                var s = sqrt(i1.toDouble()).toInt()
//                val s1 = data[i-1+(j+1)*w]+data[i+(j+1)*w]+data[i+1+(j+1)*w]-data[i-1+(j-1)*w]-data[i+(j-1)*w]-data[i+1+(j-1)*w];
//                val s2 = data[i+1+(j-1)*w]+data[i+1+(j)*w]+data[i+1+(j+1)*w]-data[i-1+(j-1)*w]-data[i-1+(j)*w]-data[i-1+(j+1)*w];
//                var s  = abs(s1) + abs(s2);

                if (s < 0)
                    s = 0
                if (s > 0xff)
                    s = 0xff
                d[i + j * w] = s
            }
        }
        image.data = d
        //return toImage(w,h,d)
        return image.toImage()
//        grayImg.setRGB(0, 0, w, h, d, 0, w);
//        return grayImg
    }

}