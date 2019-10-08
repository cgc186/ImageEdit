package second.edgeDetection

import edge.MyImage
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.abs
import kotlin.math.sqrt

class EdgeDetection {

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
            shortArrayOf(1,0,-1),
            shortArrayOf(2,0,-2),
            shortArrayOf(1,0,-1)
        )

        val sobelY = arrayOf(
            shortArrayOf(1,2,1),
            shortArrayOf(0,0,0),
            shortArrayOf(-1,-2,-1)
        )
        val size = 3
        val grayImg = ImageUtil.toGray(img)
        var w = grayImg.width
        var h = grayImg.height
        val gray = getNumPixel(grayImg)
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
                grayImg.setRGB(x,y, Color(result,result,result).rgb)
            }
        }
        return grayImg
    }

    fun sobel(img: BufferedImage): BufferedImage? {

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
                //var s = abs(s1) + abs(s2)
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
        return image.toImage()
//        grayImg.setRGB(0, 0, w, h, d, 0, w);
//        return grayImg
    }

//    fun myEdgeDetection(img: BufferedImage): BufferedImage {
//        var inArray = Array(img.width){IntArray(img.height)}
//        for (x in 0 until img.width) {
//            for (y in 0 until img.height) {
//                inArray[x][y] = img.getRGB(x, y)
//            }
//        }
//
//        var outArray = Array(img.width){IntArray(img.height)}
//        edgeDetection(inArray,outArray,img.height,img.width)
//        for (x in 0 until img.width) {
//            for (y in 0 until img.height) {
//                img.setRGB(x, y,outArray[x][y])
//            }
//        }
//        return img
//    }
//
//    private fun edgeDetection(inArray: Array<IntArray>, outArray: Array<IntArray>, height: Int, width: Int) {
//        var gx = 0
//        var gy = 0
//        val aSoble1 = arrayOf(
//            shortArrayOf(0, 1, 2),
//            shortArrayOf(-1, 0, 1),
//            shortArrayOf(-2, -1, 0)
//        )
//
//        val aSoble2 = arrayOf(
//            shortArrayOf(-2, -1, 0),
//            shortArrayOf(-1, 0, 1),
//            shortArrayOf(0, 1, 2)
//        )
//
//        for (i in 0 until height) {
//            for (j in 0 until width) {
//                gx = convolution(inArray, i, j, height, width, aSoble1, 3)
//                gy = convolution(inArray, i, j, height, width, aSoble2, 3)
//                outArray[i][j] = gx + gy
//                if (outArray[i][j] < 0) {
//                    outArray[i][j] = 0
//                } else if (outArray[i][j] > 0xff) {
//                    outArray[i][j] = 0xff;
//                }
//            }
//        }
//    }
//
//    private fun convolution(
//        inArray: Array<IntArray>,
//        row: Int,
//        column: Int,
//        height: Int,
//        width: Int,
//        filter: Array<ShortArray>,
//        filter_size: Int
//    ): Int {
//        var temp = Array(3) { IntArray(3) }
//        var rvalue = 0;
//        var x = 0
//        var y = 0
//
//        for (i in 0 until filter_size) {
//            for (j in 0 until filter_size) {
//                x = i - filter_size / 2 + row
//                y = j - filter_size / 2 + column
//                temp[i][j] = if (isInArray(x, y, height, width)) {
//                    inArray[x][y]
//                } else {
//                    0
//                }
//                rvalue += temp[i][j] * filter[i][j];
//            }
//        }
//        return rvalue;
//    }
//
//    private fun isInArray(x: Int, y: Int, height: Int, width: Int): Boolean {
//        return x in 0 until height && y in 0 until width
//    }

}