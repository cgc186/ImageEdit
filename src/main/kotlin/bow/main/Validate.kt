package bow.main

import bow.feature.Sift
import bow.model.InstanceGenerator
import bow.model.Model
import bow.util.ClassifyUtils
import bow.util.SerializationUtils
import com.sun.xml.internal.fastinfoset.util.StringArray
import weka.classifiers.Classifier

class Validate {
    private var instanceGenerator: InstanceGenerator? = null

    private val featureMaker = Sift()

    private var classifier: Classifier? = null

    private var model: Model? = null

    @Throws(Exception::class)
    private fun getCategory(categories: Array<String>, image: String): String {
        val features = featureMaker.getFeatures(image)
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
            val category = sample.getPath()?.let {
                getCategory(
                    model.categories,
                    it
                )
            }
            if (sample.getCategory().equals(category)) {
                correct++
                println("POSITIVE: " + sample.getPath())
            } else {
                println(
                    "NEGATIVE: " + sample.getPath()
                            + " -> false " + category
                )
            }
        }
        println("Precision: " + (correct.toDouble() / samples.size))
    }
}