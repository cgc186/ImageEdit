package bow.model

import java.util.*
/**
 * 单个图像样本的描述
 */
class Instance {
    /* 图像文件名 */
    private var image: String? = null

    /* 统计得到的词频 */
    private var freq: DoubleArray? = null

    /* 标注分类 */
    private var category: String? = null

    constructor(freq: DoubleArray){
        setFreq(freq)
    }

    /**
     * @return the image
     */
    fun getImage(): String? {
        return image
    }

    /**
     * @param image
     * the image to set
     */
    fun setImage(image: String) {
        this.image = image
    }

    /**
     * @return the category
     */
    fun getCategory(): String? {
        return category
    }

    /**
     * @param category
     * the category to set
     */
    fun setCategory(category: String) {
        this.category = category
    }

    /**
     * @return the freq
     */
    fun getFreq(): DoubleArray? {
        return freq
    }

    /**
     * @param freq
     * the freq to set
     */
    fun setFreq(freq: DoubleArray) {
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