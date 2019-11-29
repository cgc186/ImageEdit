package second.hough

import java.awt.Color
import java.awt.image.BufferedImage
import java.util.Arrays
import java.util.ArrayList


class LineFilter {
    private var w: Int = 0
    private var h: Int = 0
    private var halfX: Int = 0
    private var halfY: Int = 0
    private var rmax: Int = 0
    private var lineNum: Int = 0
    private var output: IntArray? = null
    private var n: IntArray? = null
    private var acc: Array<IntArray>? = null
    private var sinValue: DoubleArray? = null
    private var cosValue: DoubleArray? = null
    private var list: MutableList<Acc>? = null


    fun lineFilter(img: BufferedImage): BufferedImage {
        var data = img.getRGB(0, 0, img.width, img.height, null, 0, img.width);
        val lineDetect = lineDetect(img.width, img.height, 3, data)
        img.setRGB(0, 0, img.width, img.height, lineDetect, 0, img.width);
        return img
    }

    //Acc累加器，同一个r，theta的点进行累加，累加个数为val
    inner class Acc {
        var r = 0
        var theta = 0
        var `val` = 0
    }

    fun lineDetect(width: Int, height: Int, lineNumber: Int, input: IntArray): IntArray? {
        init(width, height, lineNumber)
        /*
        * 转换：直角坐标空间~极坐标空间
        * 不断计算累加，最终得到相同(theta,r)的像素点累加个数acc[theta][r]
        * */
        for (theta in 0..179) {
            for (x in 0 until w) {
                for (y in 0 until h) {
                    if (input[y * w + x] == Color.BLACK.rgb) {
                        var r = ((x - halfX) * cosValue!![theta] + (y - halfY) * sinValue!![theta]).toInt()
                        r += rmax//r的原本取值范围为(-ramx,ramx)，加rmax后取值范围为(0,2ramx)
                        if (r >= 0 && r < 2 * rmax)
                            acc!![theta][r]++
                    }
                }
            }
        }
        /*
        * 很重要的一步：在3*3窗口内对累加值进行极大值抑制，保留窗口内累加值最大的(theta,r)；
        * 之后将theta，r,acc[theta][r]添加进list里面；
        * 对list进行部分排序；
        * 之后取出list前面lineNum个Acc对象，通过theta和r值找出直角坐标空间的直线
        * */
        rankList(acc)
        println(".........acc个数：" + list!!.size)
        n = IntArray(lineNum)
        for (i in 0 until lineNum) {
            val acc = list!![i]
            n!![i] = drawLine(acc.r, acc.theta, n!![i])
            println("检测出的第" + i + "条直线点的累积个数：" + acc.r + "..." + acc.theta + "..." + acc.`val`)
            println("实际输出第" + i + "条直线点的个数：" + n!![i])
        }
        return output
    }

    private fun init(width: Int, height: Int, lineNumber: Int) {
        w = width
        h = height
        halfX = w / 2
        halfY = h / 2
        lineNum = lineNumber
        output = IntArray(w * h)
        val max = Math.max(w, h)
        rmax = (Math.sqrt(2.0) * max).toInt()
        acc = Array(180) { IntArray(2 * rmax) }
        list = ArrayList()
        sinValue = DoubleArray(180)
        cosValue = DoubleArray(180)
        Arrays.fill(output, Color.WHITE.rgb)
        for (theta in 0..179) {
            sinValue!![theta] = Math.sin(theta * Math.PI / 180)
            cosValue!![theta] = Math.cos(theta * Math.PI / 180)
        }
    }

    /*
    * 排序Acc数组，只对前面几个Acc进行排序，找出lineSize个较大的Acc
    * */
    private fun rankList(acc: Array<IntArray>?) {
        /*
        * 对(theta,r)进行极大值抑制,因为有时候因为计算误差或者直线不是很直的原因，
        * 同一条直线上的点转换到极坐标空间时，就会出现多对不同的(theta,r),多对不同的(theta,r)转换到直角坐标空间就出现了多条直线，
        * 这就是为什么原本图像中只有一条直线最后在该位置检测出了多条直线，因此在进行极坐标到直角坐标转换之前，
        * 有必要对(theta,r)进行极大值抑制，只保留累积值val最大的那一对(theta,r)
        * */
        for (theta in 0..179) {
            for (r in 0 until 2 * rmax) {
                val `val` = acc!![theta][r]
                var onlyLine = true
                if (`val` > 0) {
                    for (tt in -1..1) {
                        for (rr in -1..1) {
                            var newtheta = theta + tt
                            var newr = r + rr
                            if (newtheta < 0 || newtheta >= 180)
                                newtheta = 0
                            if (newr < 0 || newr >= 2 * rmax) newr = 0
                            if (acc[newtheta][newr] > `val`) onlyLine = false
                        }
                    }
                    /*
                    *在3*3窗口内累加值最大的(theta,r)我们才添加进list ，
                    * 并标记theta，r，以及累加值val
                    * */
                    if (onlyLine) {
                        val subAcc = Acc()
                        subAcc.r = r - rmax
                        subAcc.theta = theta
                        subAcc.`val` = acc[theta][r]
                        list!!.add(subAcc)
                    }
                }
            }
        }
        /*
        * 设置需要检测的直线条数为lineNum,
        * 按val值大小升序排列list，当然只需要进行前面部分的排序即可
        * */
        for (i in 0 until lineNum) {
            var max = i
            for (j in i + 1 until list!!.size) {
                if (list!![j].`val` > list!![max].`val`) {
                    max = j
                }
            }
            if (max != i) {
                val accmax = list!![max]
                val acci = list!![i]
                list!![max] = acci
                list!![i] = accmax
            }
        }
    }

    /*
    *转换：极坐标空间~直角坐标空间
    *r=(x-halfx)*cos(theta)+(y-halfy)*sin(theta);
    * 已知r，theta，x或者y的情况下，通过该式计算出符合条件的y或者x。
    * 画出lineNum条直线
    * */
    private fun drawLine(r: Int, theta: Int, n: Int): Int {
        var n = n
        if (theta >= 45 && theta <= 135) {
            for (x in 0 until w) {
                val y = ((r - (x - halfX) * cosValue!![theta]) / sinValue!![theta]).toInt() + halfY
                if (y >= 0 && y < h) {
                    output?.set(y * w + x, Color.BLACK.rgb)
                    n++
                }
            }
        } else {
            for (y in 0 until h) {
                val x = ((r - (y - halfY) * sinValue!![theta]) / cosValue!![theta]).toInt() + halfX
                if (x >= 0 && x < w) {
                    output?.set(y * w + x, Color.BLACK.rgb)
                    n++
                }
            }
        }
        return n
    }

}