package bow.model

import java.util.*

/**
 * 单个图像样本的描述
 */
class Instance {
    /* 图像文件名 */
    var image: String = ""

    /* 统计得到的词频 */
    var freq: DoubleArray? = null

    /* 标注分类 */
    var category: String = ""

    constructor(freq: DoubleArray) {
        this.freq = freq
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    override fun toString(): String {
        return "Instance [freq=" + Arrays.toString(freq) + "]"
    }

}