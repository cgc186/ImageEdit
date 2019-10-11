package second.hough

import java.awt.Image
import java.awt.image.BufferedImage
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class HoughLineFilter {
    private var houghSpace = 500  //霍夫空间
    private var hough2d: Array<IntArray>? = null
    private var width: Int = 0
    private var height: Int = 0

    var threshold: Float = 0.toFloat()
    var scale: Float = 0.toFloat()
    var offset: Float = 0.toFloat()

    class Line(var row: Int, var col: Int)

    init {
        threshold = 0.7f //  default hough transform parameters 默认hough变换参数
        scale = 1.0f     //  scale = 1.0f;                      规模
        offset = 0.0f    //  offset = 0.0f;                     抵消
    }

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

        // define temp hough 2D array and initialize the hough 2D
        // 定义临时hough 2d数组并初始化该数组
        hough2d = Array(houghSpace) { IntArray(2 * maxLength) }

        // start hough transform now....
        // 现在开始霍夫变换…
        val image2d = convert1Dto2D(inPixels)
        for (row in 0 until height) {
            for (col in 0 until width) {
                if ((image2d[row][col] and 0xff) == 0) {
                    continue // which means background color 也就是说背景色
                }
                // since we does not know the theta angle and r value,       因为我们不知道θ角和r值，
                // we have to calculate all hough space for each pixel point 我们必须计算每个像素点的所有hough空间
                // then we got the max possible theta and r pair.            然后得到最大可能的θ和r对。
                // r = x * cos(theta) + y * sin(theta)
                for (cell in 0 until houghSpace) {
                    max =
                        ((col - centerX) * cos(cell * houghInterval) + (row - centerY) * sin(cell * houghInterval)).toInt() + maxLength // start from zero, not (-max_length)                  从零开始，不是（-max_length）
                    if (max < 0 || max >= 2 * maxLength) {// make sure r did not out of scope[0, 2*max_lenght] 确保R不超出[0, 2*max_lenght]范围
                        continue
                    }
                    hough2d!![cell][max]++
                }
            }
        }

        // find the max hough value  求最大hough值
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
                // discard it 丢弃它
                val houghValue = hough2d!![row][col]
                var isLine = true
                for (i in -1..1) {
                    for (j in -1..1) {
                        if (i != 0 || j != 0) {
                            var yf = row + i
                            val xf = col + j
                            if (0 < xf && xf < 2 * maxLength) {
                                if (yf < 0) {
                                    yf += houghSpace
                                }
                                if (yf >= houghSpace) {
                                    yf -= houghSpace
                                }
                                if (hough2d!![yf][xf] <= houghValue) {
                                    continue
                                }
                                isLine = false
                                break
                            }
                        }
                    }
                }
                if (!isLine) continue

                result.add(Line(row, col))

//                val dy = sin(row * houghInterval)
//
//                if (line.containsKey(dy)) {
//                    val value = line.getValue(dy)
//                    if (value.containsKey(row)) {
//                        value[row]?.add(col)
//                    } else {
//                        var set = mutableSetOf<Int>()
//                        set.add(col)
//                        value[row] = set
//                    }
//                } else {
//                    var set = mutableSetOf<Int>()
//                    set.add(col)
//                    var map = mutableMapOf<Int, MutableSet<Int>>()
//                    map[row] = set
//                    line[dy] = map
//                }

                //println("row = $row col = $col dy = $dy dx = $dx")

            }
        }


//        line.map { m ->
//
//            m.value.map { mm ->
//                var maxHough = hough2d!![mm.key][mm.value.elementAt(0)]
//                var row = mm.key
//                var col = mm.value.elementAt(0)
//                mm.value.forEach {
//                    var hough = hough2d!![mm.key][it]
//                    if (maxHough < hough) {
//                        col = it
//                    }
//                }
//                result.add(Line(row, col))
//            }
//        }

        result.sortedBy { it.row }.forEach { }

        result.forEach {
            println("row:${it.row} col:${it.col}")
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
            var row = line.row
            var col = line.col
            var maxHough = hough2d!![row][col]

            temp.forEach {
                var hough = hough2d!![it.row][it.col]
                if (maxHough < hough) {
                    var row = it.row
                    var col = it.col
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
            val dy = sin(it.row * houghInterval)
            val dx = cos(it.row * houghInterval)

            println("row = ${it.row} col = ${it.col} dy = $dy dx = $dx")

            if (it.row <= houghSpace / 4 || it.row >= 3 * houghSpace / 4) {
                for (subRow in 0 until height) {
                    val subCol =
                        ((it.col.toDouble() - maxLength.toDouble() - (subRow - centerY) * dy) / dx).toInt() + centerX
                    if (subCol in 0 until width) {
                        image2d[subRow][subCol] = 0xffff0000.toInt()
                    }
                }
            } else {
                for (subCol in 0 until width) {
                    val subRow =
                        ((it.col.toDouble() - maxLength.toDouble() - (subCol - centerX) * dx) / dy).toInt() + centerY
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
        var size = 6
        for (i in -size..size) {
            for (j in -size..size) {
                if (i != 0 || j != 0) {
                    if (a.row + i == b.row && a.col + j == b.col) {
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