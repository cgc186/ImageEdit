package bow.model

class TrainResult {
    /* 样本列表 */
    private var instances: List<Instance>? = null

    /* 词典 */
    private var words: List<Feature>? = null

    constructor(instances: List<Instance>, words: MutableList<Feature>?) {
        this.instances = instances
        this.words = words
    }

    /**
     * @return the instances
     */
    fun getInstances(): List<Instance>? {
        return instances
    }

    /**
     * @param instances
     * the instances to set
     */
    fun setInstances(instances: List<Instance>) {
        this.instances = instances
    }

    /**
     * @return the words
     */
    fun getWords(): List<Feature>? {
        return words
    }

    /**
     * @param words
     * the words to set
     */
    fun setWords(words: List<Feature>) {
        this.words = words
    }
}