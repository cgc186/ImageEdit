package second.hough

import java.awt.image.BufferedImage
import com.sun.scenario.effect.ImageData
import java.awt.Color
import java.awt.Image
import kotlin.math.cos
import kotlin.math.sin
import java.util.ArrayList
import kotlin.math.sqrt


class HoughLine {

    class Line(var ro: Int, var angle: Int) {
    }

    fun hough(img: BufferedImage): BufferedImage {
        val h = img.height
        val w = img.width
        var imgData = img.getRGB(0, 0, w, h, null, 0, w)
        val data = IntArray(w * h)

        for (i in imgData.indices) {
            data[i] = imgData[i] and 0xff
        }

        val ro = sqrt((h * h + w * w).toDouble()).toInt()
        val theta = 180
        val hist = Array(ro) { IntArray(theta) }

        for (k in 0 until theta) {
            for (i in 0 until h) {
                for (j in 0 until w) {
                    if (data[j + i * w] != 0) {
                        val rho =
                            (j * cos(k * Math.PI / (theta * 2)) + i * sin(k * Math.PI / (theta * 2))).toInt()
                        hist[rho][k]++
                    }
                }
            }
        }

        val index = maxIndex(hist, 70) //找到大于最大值*0.7的二维直方图的点

        for (k in 0 until index.size) {
            val resTheta = index[k].angle * Math.PI / (theta * 2)

            for (i in 0 until h) {
                for (j in 0 until w) {
                    val rho = (j * cos(resTheta) + i * sin(resTheta)).toInt()
                    if (data[j + i * w] != 0 && rho == index[k].ro) {
                        data[j + i * w] = 0xffff0000.toInt()  //在直线上的点设为红色
                    } else {
                        data[j + i * w] =
                            255 shl 24 or (data[j + i * w] shl 16) or (data[j + i * w] shl 8) or data[j + i * w]
                    }
                }
            }
        }
        val image = BufferedImage(img.width, img.height, Image.SCALE_DEFAULT)

        image.setRGB(0, 0, img.width, img.height, data, 0, img.width);

        return image
    }

    private fun maxIndex(hist: Array<IntArray>, i: Int): ArrayList<Line> {
        val `in` = ArrayList<Line>()
        var max = 0

        for (i1 in hist.indices) {
            for (j1 in hist[i1].indices) {
                if (max < hist[i1][j1]) {
                    max = hist[i1][j1]
                }
            }
        }
        println(max)

        for (i1 in hist.indices) {
            for (j1 in hist[i1].indices) {
                if (hist[i1][j1] > max * (i / 100.0))
                    `in`.add(Line(i1, j1))
            }
        }

        return `in`
    }
}