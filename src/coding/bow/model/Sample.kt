package bow.model
/**
 * 样本文件描述
 */
class Sample {
    /* 路径 */
    var path: String = ""

    /* 分类标注 */
    var category: String = ""

    constructor(path: String) {
        this.path = path
    }

    constructor(path: String, cate: String) {
        this.path = path
        this.category = cate
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    override fun toString(): String {
        return "Sample [path=$path, category=$category]"
    }

}