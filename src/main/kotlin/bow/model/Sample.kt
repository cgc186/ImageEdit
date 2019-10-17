package bow.model
/**
 * 样本文件描述
 */
class Sample {
    /* 路径 */
    private var path: String? = null

    /* 分类标注 */
    private var category: String? = null

    constructor(){

    }

    constructor(path: String) {
        this.path = path
    }

    constructor(path: String, cate: String) {
        this.path = path
        this.category = cate
    }

    /**
     * @return the path
     */
    fun getPath(): String? {
        return path
    }

    /**
     * @param path
     * the path to set
     */
    fun setPath(path: String) {
        this.path = path
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

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    override fun toString(): String {
        return "Sample [path=$path, category=$category]"
    }

}