package bow.model

/**
 * 训练结果的描述
 */
class TrainResult {
    /* 样本列表 */
    var instances: List<Instance>

    /* 词典 */
    var words: List<Feature>

    constructor(instances: List<Instance>, words: List<Feature>) {
        this.instances = instances
        this.words = words
    }
}