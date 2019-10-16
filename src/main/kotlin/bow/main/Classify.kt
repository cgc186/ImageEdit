package bow.main

import bow.feature.Sift
import bow.model.InstanceGenerator
import bow.model.Model
import bow.util.ClassifyUtils
import bow.util.SerializationUtils

/**
 * 分类程序入口
 */
class Classify {
    private val featureMaker = Sift()

    fun classify(inputImg:String,inputModel:String){
        val model: Model = SerializationUtils.loadObject(inputModel) as Model
        val classifier = model.classifier
        val categories = model.categories
        val instanceGenerator = InstanceGenerator()
        val features = featureMaker.getFeatures(inputImg)
        val instance = instanceGenerator.getInstance(
            features,
            model.words
        )
        val value = classifier.classifyInstance(
            ClassifyUtils
                .getWekaInstance(instance)
        )
        val category = categories[value.toInt()]
        println(category)
    }
}