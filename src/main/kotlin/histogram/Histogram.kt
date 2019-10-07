package histogram

import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.round

class Histogram {
    private fun getNumPixel(img: BufferedImage): IntArray {
        var numPixel = IntArray(256) { 0 }
        for (x in 0 until img.width) {
            for (y in 0 until img.height) {
                val c = Color(img.getRGB(x, y))
                var r = c.red
                numPixel[r]++
                var g = c.green
                numPixel[g]++
                var b = c.blue
                numPixel[b]++
            }
        }
        for (pos in 0 until 256) {
            numPixel[pos] /= 3
        }
        return numPixel
    }

    fun histogram(img: BufferedImage): BufferedImage {
        val numPixel = getNumPixel(img)

        // 灰度直方图的累积分布
        val probPixel = DoubleArray(256) { 0.0 }
        // 均衡化结果图
        val pixel = DoubleArray(256) { 0.0 }

        var count = 0
        val l = 256
        val numOfPixels = (img.height * img.width).toDouble()

        for (pos in 0 until 256) {
            count += numPixel[pos]
            probPixel[pos] = 1.0 * count / numOfPixels
            pixel[pos] = round(probPixel[pos]*(l-1))
        }

        for (i in 1 until img.width) {
            for (j in 1 until img.height) {
                val c = Color(img.getRGB(i, j))
                var r = c.red
                var g = c.green
                var b = c.blue
                img.setRGB(i, j, Color(pixel[r].toInt(), pixel[g].toInt(), pixel[b].toInt()).rgb)
            }
        }
        return img
    }
}