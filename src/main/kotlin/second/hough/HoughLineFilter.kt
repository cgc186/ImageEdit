package second.hough

import java.awt.Image
import java.awt.image.BufferedImage
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class HoughLineFilter {
    private var houghSpace = 720
    private var hough1d: IntArray? = null
    private var hough2d: Array<IntArray>? = null
    private var width: Int = 0
    private var height: Int = 0

    var threshold: Float = 0.toFloat()
    var scale: Float = 0.toFloat()
    var offset: Float = 0.toFloat()

    init {
        threshold = 0.7f //  default hough transform parameters
        scale = 1.0f     //  scale = 1.0f;
        offset = 0.0f    //  offset = 0.0f;
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
        val houghInterval = PI_VALUE / houghSpace.toDouble()

        var max = width.coerceAtLeast(height)
        val maxLength = (sqrt(2.0) * max).toInt()
        hough1d = IntArray(2 * houghSpace * maxLength)

        // define temp hough 2D array and initialize the hough 2D
        hough2d = Array(houghSpace) { IntArray(2 * maxLength) }
        for (i in 0 until houghSpace) {
            for (j in 0 until 2 * maxLength) {
                hough2d!![i][j] = 0
            }
        }

        // start hough transform now....
        val image2d = convert1Dto2D(inPixels)
        for (row in 0 until height) {
            for (col in 0 until width) {
                val p = image2d[row][col] and 0xff
                if (p == 0) continue // which means background color

                // since we does not know the theta angle and r value,
                // we have to calculate all hough space for each pixel point
                // then we got the max possible theta and r pair.
                // r = x * cos(theta) + y * sin(theta)
                for (cell in 0 until houghSpace) {
                    max =
                        ((col - centerX) * cos(cell * houghInterval) + (row - centerY) * sin(cell * houghInterval)).toInt()
                    max += maxLength // start from zero, not (-max_length)
                    if (max < 0 || max >= 2 * maxLength) {// make sure r did not out of scope[0, 2*max_lenght]
                        continue
                    }
                    hough2d!![cell][max] += 1
                }
            }
        }

        // find the max hough value
        var maxHough = 0
        for (i in 0 until houghSpace) {
            for (j in 0 until 2 * maxLength) {
                hough1d!![i + j * houghSpace] = hough2d!![i][j]
                if (hough2d!![i][j] > maxHough) {
                    maxHough = hough2d!![i][j]
                }
            }
        }
        println("MAX HOUGH VALUE = $maxHough")

        // transfer back to image pixels space from hough parameter space
        val houghThreshold = (threshold * maxHough).toInt()
        for (row in 0 until houghSpace) {
            for (col in 0 until 2 * maxLength) {
                if (hough2d!![row][col] < houghThreshold)
                // discard it
                    continue
                val houghValue = hough2d!![row][col]
                var isLine = true
                for (i in -1..1) {
                    for (j in -1..1) {
                        if (i != 0 || j != 0) {
                            var yf = row + i
                            val xf = col + j
                            if (xf < 0) continue
                            if (xf < 2 * maxLength) {
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

                // transform back to pixel data now...
                val dy = sin(row * houghInterval)
                val dx = cos(row * houghInterval)
                if (row <= houghSpace / 4 || row >= 3 * houghSpace / 4) {
                    for (subRow in 0 until height) {
                        val subCol =
                            ((col.toDouble() - maxLength.toDouble() - (subRow - centerY) * dy) / dx).toInt() + centerX
                        if (subCol in 0 until width) {
                            image2d[subRow][subCol] = -16776961
                        }
                    }
                } else {
                    for (subCol in 0 until width) {
                        val subRow =
                            ((col.toDouble() - maxLength.toDouble() - (subCol - centerX) * dx) / dy).toInt() + centerY
                        if (subRow in 0 until height) {
                            image2d[subRow][subCol] = -16776961
                        }
                    }
                }
            }
        }

        // convert to hough 1D and return result
        for (i in this.hough1d!!.indices) {
            val value = clamp((scale * this.hough1d!![i] + offset).toInt()) // scale always equals 1
            this.hough1d!![i] = -0x1000000 or value + (value shl 16) + (value shl 8)
        }

        // convert to image 1D and return
        for (row in 0 until height) {
            for (col in 0 until width) {
                outPixels[col + row * width] = image2d[row][col]
            }
        }
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

    companion object {
        const val PI_VALUE = Math.PI

        fun clamp(value: Int): Int {
            var value = value
            if (value < 0)
                value = 0
            else if (value > 255) {
                value = 255
            }
            return value
        }
    }

}