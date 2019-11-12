package bow.model

import com.alibaba.simpleimage.analyze.sift.scale.KDFeaturePoint
import java.io.Serializable
import java.lang.Math.pow
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * SIFT 特征点的向量描述
 */
class Feature : Serializable {

    companion object {
        private const val serialVersionUID = -4432933599973324376L
        /* 特征维数 */
        val DIMENSION = 128
    }

    /* 特征向量的值 */
    var values: IntArray

    constructor(point: KDFeaturePoint) {
        values = point.descriptor
    }

    constructor(isRandom: Boolean) {
        values = IntArray(DIMENSION)
        if (isRandom) {
            val random = Random()
            for (i in 0 until DIMENSION) {
                values[i] = random.nextInt(255)
            }
        }
    }

    /**
     * 计算与其它特征向量的欧式距离
     *
     * @param x
     * @return
     */
    fun distance(x: Feature): Double {
        val values2 = x.values
        if (values == null || values2 == null) {
            return java.lang.Double.NaN
        }
        var tmp = 0.0
        for (i in 0 until DIMENSION) {
            tmp += (values!![i] - values2[i]).toDouble().pow(2.0)
        }
        return sqrt(tmp)
    }

    /**
     * 与其它特征向量做加法
     *
     * @param x
     */
    fun add(x: Feature) {
        val values2 = x.values
        if (values == null || values2 == null) {
            return
        }
        for (i in 0 until DIMENSION) {
            values!![i] += values2[i]
        }
    }

    fun divide(x: Int) {
        if (values == null) {
            return
        }
        for (i in 0 until DIMENSION) {
            values[i] /= x
        }
    }

    /*
     * 输出特征值
     * @see java.lang.Object#toString()
     */
    override fun toString(): String {
        return "Feature [values=" + Arrays.toString(values) + "]"
    }
}