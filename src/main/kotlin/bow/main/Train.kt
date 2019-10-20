package bow.main

import bow.model.Feature
import bow.model.Instance
import bow.model.InstanceGenerator
import bow.model.Model
import bow.util.ClassifyUtils
import bow.util.SerializationUtils
import weka.classifiers.Classifier
import weka.classifiers.functions.MultilayerPerceptron


/**
 * 训练程序入口
 */
object Train {
    fun train(
        imgBase: String,
        categories: Array<String>,
        cateSample: Int,
        outputArff: String,
        outputClassifier: String,
        outputModel: String
    ) {
        //图像样本生成
        val instanceGenerator = InstanceGenerator(categories)
        //训练结果
        val trainResult = instanceGenerator.train(imgBase, cateSample)
        //实例
        val instances = trainResult.instances
        println("dumping arff to $outputArff")
        //保存到...

        instanceGenerator.dumpArff(instances, outputArff)
        //使用mlp运行交叉验证
        println("running cross-validation using MLP")

        val arguments = ("-t " + outputArff + " -d " + outputClassifier
                + " -L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a")

        //对实例进行分类
        MultilayerPerceptron.main(arguments.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
        val words = trainResult.words
        val classifier: Classifier = ClassifyUtils.loadClassifier(outputClassifier)
        val model =
            Model(
                categories, words as MutableList<Feature>, classifier
            )
        SerializationUtils.dumpObject(outputModel, model)
        println("model saved as $outputModel")
    }
}