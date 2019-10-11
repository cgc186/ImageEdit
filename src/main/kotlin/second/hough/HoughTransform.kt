package second.hough

import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.*

class HoughTransform {

    class Line(var thetaDim: Int, var distDim: Int) {
        fun print(): String {
            return "thetaDim:$thetaDim ,distDim:$distDim"
        }
    }

    fun linesDetectorHough(img: BufferedImage) {
        val w = img.width
        val h = img.height
        val thetaDim = 90  //hough空间中theta轴的刻度数量(将[0,pi)均分为多少份),反应theta轴的粒度,越大粒度越细
        var distStep = 1   //hough空间中dist轴的划分粒度,即dist轴的最小单位长度
        var maxDist = sqrt((w * w + h * h).toDouble())  //对角线长度 最大距离
        var distDim = ceil(maxDist / distStep).toInt()    //刻度数量
        var halfDistWindowSize = (distDim / 50).toInt()
        val halfThetaWindowSize = 2
        var accumulator = Array(thetaDim) { IntArray(distDim) }

        var sinTheta = IntArray(thetaDim) {
            sin((it.toDouble()) * Math.PI / (thetaDim.toDouble())).toInt()
        }

        var cosTheta = IntArray(thetaDim) {
            cos((it.toDouble()) * Math.PI / (thetaDim.toDouble())).toInt()
        }

        for (i in 1 until w) {
            for (j in 1 until h) {
                //val c = Color(img.getRGB(i, j))
                if ((img.getRGB(i, j)and 0xFFFFFF) == 0xFFFFFF) {
                    for (k in 0 until thetaDim) {
                        accumulator[k][round((i * cosTheta[k] + j * sinTheta[k]) * distDim / maxDist).toInt()]++
                    }
                }
            }
        }

        var max = 0
        for (i in accumulator.indices) {
            for (j in accumulator[0].indices) {
                if (accumulator[i][j] > max) {
                    max = accumulator[i][j]
                }
            }
        }

        val threshold = (max * 2.3875 / 10).toInt()

        println("threshold: $threshold")

        println("accumulator:")

//        accumulator.map { arr ->
//            arr.forEach {
//                println(it)
//            }
//        }

        var result = mutableSetOf<Line>()

        for (i in accumulator.indices) {
            for (j in accumulator[i].indices) {
                if (accumulator[i][j] > threshold) {
                    result.add(Line(i, j))
                }
            }
        }

        println("result:")

//        result.forEach {
//            println(it.print())
//        }

        var temp = mutableSetOf<Line>()
        for (i in result.indices) {
            val eightNeiborhood = getSection(
                accumulator,
                max(
                    0,
                    result.elementAt(i).thetaDim - halfThetaWindowSize + 1
                ), min(
                    result.elementAt(i).thetaDim + halfThetaWindowSize,
                    accumulator.size
                ),
                max(
                    0,
                    result.elementAt(i).distDim - halfDistWindowSize + 1
                ), min(
                    result.elementAt(i).distDim + halfDistWindowSize,
                    accumulator[0].size
                )
            )
            println("${result.elementAt(i).thetaDim} , ${result.elementAt(i).distDim}")

            println("f:"+max(
                0,
                result.elementAt(i).thetaDim - halfThetaWindowSize + 1
            ))
            println("e:"+min(
                result.elementAt(i).thetaDim + halfThetaWindowSize,
                accumulator.size
            ))
            println("f:"+max(
                0,
                result.elementAt(i).distDim - halfDistWindowSize + 1
            ))
            println("e:"+min(
                result.elementAt(i).distDim + halfDistWindowSize,
                accumulator[0].size
            ))

            println("eightNeiborhood:::")

            eightNeiborhood.map { arr ->
                arr.forEach {
                    print("$it ")
                }
                println()
            }
            println("end")
            println()

            if (bigThanAll(accumulator[result.elementAt(i).thetaDim][result.elementAt(i).distDim], eightNeiborhood)) {
                temp.add(result.elementAt(i))
            }
        }

        temp.forEach {
            println(it.print())
        }

        //result = toArray(temp)
    }

//    private fun toArray(temp: Set<Line>): Array<IntArray> {
//        val size = temp.size
//        val array = Array(size) { IntArray(size) }
//        for (i in 0 until size){
//            for (j in size)
//        }
//    }

    private fun getSection(arr: Array<IntArray>, ofd: Int, oed: Int, tfd: Int, tod: Int): Array<IntArray> {
        var result = Array(oed - ofd) { IntArray(tod - tfd) }
        for (i in result.indices) {
            for (j in result[i].indices) {
                result[i][j] = arr[i][j]
            }
        }
        return result
    }

    private fun bigThanAll(num: Int, arr: Array<IntArray>): Boolean {
        var flag = true
        for (i in arr.indices) {
            for (j in arr[i].indices) {
                if (num < arr[i][j]) {
                    return false
                }
            }
        }
        return flag
    }
}


