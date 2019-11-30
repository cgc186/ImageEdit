package bow.model

import weka.classifiers.Classifier
import java.io.Serializable

/**
 * 训练好的分类器模型描述
 */
class Model(/* 分类器包含的类别 */
    var categories: Array<String>, /* 词典 */
    var words: List<Feature>, /* 分类器实例 */
    var classifier: Classifier
) : Serializable {
    companion object {

        private const val serialVersionUID = 9155043182593064253L
    }

}