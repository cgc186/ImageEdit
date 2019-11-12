package bow.main

import bow.feature.Sift
import bow.model.InstanceGenerator
import bow.model.Model
import bow.util.ClassifyUtils
import bow.util.SerializationUtils
import weka.classifiers.Classifier
/**
 * 评估程序入口
 */
object Validate {
    private var instanceGenerator: InstanceGenerator? = null

    private val featureMaker = Sift()

    private var classifier: Classifier? = null

    private var model: Model? = null

    @Throws(Exception::class)
    private fun getCategory(categories: Array<String>, image: String): String {
        val features = featureMaker.getFeature(image)
        val instance = instanceGenerator!!.getInstance(
            features,
            model!!.words
        )
        val value = classifier!!.classifyInstance(
            ClassifyUtils
                .getWekaInstance(instance)
        )
        return categories[value.toInt()]
    }

    fun validate(
        imgBase: String, validateCategories: Array<String>,
        start: Int, end: Int, inputModel: String
    ) {
        instanceGenerator = InstanceGenerator(validateCategories)
        val model :Model= SerializationUtils.loadObject(inputModel) as Model
        classifier = model.classifier
        val samples = instanceGenerator!!
            .getSamples(imgBase, start, end)
        var correct = 0
        for (sample in samples) {
            val category = sample.path?.let {
                getCategory(
                    model.categories,
                    it
                )
            }
            if (sample.category == category) {
                correct++
                println("POSITIVE: " + sample.path)
            } else {
                println(
                    "NEGATIVE: " + sample.path
                            + " -> false " + category
                )
            }
        }
        println("Precision: " + (correct.toDouble() / samples.size))
    }
}

fun main() {
    val imgBase = "E:\\编程\\kotlin\\images\\training"
    //验证类别
    val validateCategories = arrayOf("Phoning", "PlayingGuitar", "RidingBike", "RidingHorse", "Running", "Shooting")

    val inputModel = ""

    Validate.validate(imgBase,validateCategories,0,80,inputModel)
}