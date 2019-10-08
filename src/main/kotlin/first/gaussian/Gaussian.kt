package first.gaussian


import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.exp

class Gaussian {

    fun myGaussianFilter(img: BufferedImage, n: Int, sigma: Float): BufferedImage {

        var kernel: Array<FloatArray> = getKernel(n, sigma)

        return gaussian(img, kernel, 2 * n + 1)
    }

    private var sum = 0f
    private fun getKernel(n: Int, sigma: Float): Array<FloatArray> {
        var sum = 0f
        val size = 2 * n + 1
        val sigma22 = 2f * sigma * sigma
        val sigma22PI = Math.PI.toFloat() * sigma22
        val kernelData = Array(size) { FloatArray(size) }

        for ((row, i) in (-n..n).withIndex()) {
            for ((column, j) in (-n..n).withIndex()) {
                val xDistance = (i * i).toFloat()
                val yDistance = (j * j).toFloat()
                kernelData[row][column] =
                    exp((-(xDistance + yDistance) / sigma22).toDouble()).toFloat() / sigma22PI
            }
        }
        for (i in 0 until size) {
            for (j in 0 until size) {
                sum += kernelData[i][j]
            }
        }
        for (i in kernelData.indices) {
            for (j in kernelData.indices) {
                kernelData[i][j] = kernelData[i][j] / sum
            }
        }
        return kernelData
    }

    private fun gaussian(img: BufferedImage, kernel: Array<FloatArray>, _size: Int): BufferedImage {
        //var temp = img.getScaledInstance(img.width, img.height, Image.SCALE_DEFAULT)
        //val imgTemp :BufferedImage = (temp as (sun.awt.image.ToolkitImage)).bufferedImage
        for (i in 0 until img.width) {
            for (j in 0 until img.height) {

                // [2] 找到图像输入点f(i,j),以输入点为中心与核中心对齐
                //     核心为中心参考点 卷积算子=>高斯矩阵180度转向计算
                //     x y 代表卷积核的权值坐标   i j 代表图像输入点坐标
                //     卷积算子     (f*g)(i,j) = f(i-k,j-l)g(k,l)          f代表图像输入 g代表核
                //     带入核参考点 (f*g)(i,j) = f(i-(k-ai), j-(l-aj))g(k,l)   ai,aj 核参考点
                //     加权求和  注意：核的坐标以左上0,0起点
                var sum = 0.0F
                var r = 0.0F
                var g = 0.0F
                var b = 0.0F
                var a = 0.0F
                for (k in 0 until _size) {
                    for (l in 0 until _size) {
                        val x = i - k + (_size / 2)
                        val y = j - l + (_size / 2)
                        if (x < 0 || x >= img.width)
                            continue
                        if (y < 0 || y >= img.height)
                            continue
                        sum += kernel[k][l]
                        val c = Color(img.getRGB(x, y))
                        r += c.red * kernel[k][l]
                        g += c.green * kernel[k][l]
                        b += c.blue * kernel[k][l]
                        a += c.alpha * kernel[k][l]
                    }
                }
                // 放入中间结果,计算所得的值与没有计算的值不能混用
                r /= sum
                g /= sum
                b /= sum
                a /= sum
                img.setRGB(i, j, Color(r.toInt(), g.toInt(), b.toInt(), a.toInt()).rgb)
            }
        }
        return img
    }
}