package second.hough

import java.awt.Image
import java.awt.image.BufferedImage
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class HoughLineFilter2 {
    private var houghSpace = 1800  //霍夫空间
    private var hough2d: Array<IntArray>? = null
    private var width: Int = 0
    private var height: Int = 0

    val threshold: Float = 0.30f  //默认hough变换参数
    val size = 50

    class Line(var theta: Int, var r: Int)

    fun filter(src: BufferedImage): BufferedImage? {
        var dest = BufferedImage(src.width, src.height, Image.SCALE_DEFAULT)
        width = src.width
        height = src.height

        var outPixels = IntArray(width * height)
        var inPixels = src.getRGB(0, 0, width, height, null, 0, width)

        houghTransform(inPixels, outPixels)

        dest.setRGB(0, 0, width, height, outPixels, 0, width)
        return dest
    }

    private fun houghTransform(inPixels: IntArray, outPixels: IntArray) {
        // prepare for hough transform
        val centerX = width / 2
        val centerY = height / 2
        val houghInterval = Math.PI / houghSpace.toDouble()

        var max = width.coerceAtLeast(height)
        val maxLength = (sqrt(2.0) * max).toInt()

        // 定义临时hough 2d数组并初始化该数组
        hough2d = Array(houghSpace) { IntArray(2 * maxLength) }

        // 现在开始霍夫变换…
        val image2d = convert1Dto2D(inPixels)
        for (row in 0 until height) {
            for (col in 0 until width) {
                if ((image2d[row][col] and 0xff) == 0) {
                    continue //  也就是说背景色不是黑色的
                }
                // 因为我们不知道θ角和r值，所以我们必须计算每个像素点的所有hough空间,然后得到最大可能的θ和r对。
                // r = x * cos(theta) + y * sin(theta)
                for (cell in 0 until houghSpace) {
                    max =
                        ((col - centerX) * cos(cell * houghInterval) + (row - centerY) * sin(cell * houghInterval)).toInt() + maxLength // 从零开始，不是（-max_length）
                    if (max < 0 || max >= 2 * maxLength) {// 确保R不超出[0, 2*max_lenght]范围
                        continue
                    }
                    hough2d!![cell][max]++
                }
            }
        }

        // 求最大hough值
        var maxHough = 0
        for (i in 0 until houghSpace) {
            for (j in 0 until 2 * maxLength) {
                if (hough2d!![i][j] > maxHough) {
                    maxHough = hough2d!![i][j]
                }
            }
        }
        println("MAX HOUGH VALUE = $maxHough")

        var line =
            mutableMapOf<Double, MutableMap<Int, MutableSet<Int>>>()

        val result = mutableSetOf<Line>()

        // transfer back to image pixels space from hough parameter space
        val houghThreshold = (threshold * maxHough).toInt()
        for (row in 0 until houghSpace) {
            for (col in 0 until 2 * maxLength) {
                if (hough2d!![row][col] < houghThreshold) continue
                result.add(Line(row, col))
            }
        }

        var temp = mutableSetOf<Line>()

        var tResult = mutableSetOf<Line>()

        var i = 0
        var e = 0
        while (i < result.size - 1) {
            temp.add(result.elementAt(i))

            for (j in i + 1 until result.size) {
                //println("j:$j")
                if (isAdjacent(result.elementAt(i), result.elementAt(j))) {
                    //println("add")
                    temp.add(result.elementAt(j))
                    e = j
                } else {
                    i = j
                    break
                }
            }
            val line = temp.elementAt(0)
            var row = line.theta
            var col = line.r
            var maxHough = hough2d!![row][col]

            temp.forEach {
                var hough = hough2d!![it.theta][it.r]
                if (maxHough < hough) {
                    var row = it.theta
                    var col = it.r
                }
            }
            temp.clear()
            //println("result add row:$row col:$col")
            tResult.add(Line(row, col))
            if (e == result.size - 1) {
                break
            }
        }

        tResult.forEach {
            val dy = sin(it.theta * houghInterval)
            val dx = cos(it.theta * houghInterval)

            println("theta = ${it.theta} r = ${it.r} sin = $dy cos = $dx")

            println("y = ${-(dx / dy)} x + ${(it.r / dy)}")

            if (it.theta <= houghSpace / 4 || it.theta >= 3 * houghSpace / 4) {
                for (subRow in 0 until height) {
                    val subCol =
                        ((it.r.toDouble() - maxLength.toDouble() - (subRow - centerY) * dy) / dx).toInt() + centerX
                    if (subCol in 0 until width) {
                        image2d[subRow][subCol] = 0xffff0000.toInt()
                    }
                }
            } else {
                for (subCol in 0 until width) {
                    val subRow =
                        ((it.r.toDouble() - maxLength.toDouble() - (subCol - centerX) * dx) / dy).toInt() + centerY
                    if (subRow in 0 until height) {
                        image2d[subRow][subCol] = 0xffff0000.toInt()
                    }
                }
            }
        }

        // convert to image 1D and return
        for (row in 0 until height) {
            for (col in 0 until width) {
                outPixels[col + row * width] = image2d[row][col]
            }
        }
    }

    private fun isAdjacent(a: Line, b: Line): Boolean {
        for (i in -size..size) {
            for (j in -size..size) {
                if (i != 0 || j != 0) {
                    if (a.theta + i == b.theta && a.r + j == b.r) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun convert1Dto2D(pixels: IntArray): Array<IntArray> {
        val image2d = Array(height) { IntArray(width) }
        var index = 0
        for (row in 0 until height) {
            for (col in 0 until width) {
                index = row * width + col
                image2d[row][col] = pixels[index]
            }
        }
        return image2d
    }
}