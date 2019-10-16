package bow.main

import bow.model.Instance
import bow.model.InstanceGenerator
import bow.model.Model
import bow.util.ClassifyUtils
import bow.util.SerializationUtils
import weka.classifiers.Classifier
import weka.classifiers.functions.MultilayerPerceptron

class Train{
    fun train(){
        val imgBase = ""
        val categories = ""
        val cateSample = 1
        val outputArff = ""
        val outputClassifier = ""
        val outputModel = ""
        val instanceGenerator = InstanceGenerator(
            categories.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        )
        val trainResult = instanceGenerator.train(imgBase, cateSample)
        val instances = trainResult.getInstances()
        println("dumping arff to $outputArff")
        instanceGenerator.dumpArff(instances as ArrayList<Instance>, outputArff)
        println("running cross-validation using MLP")
        val arguments = ("-t " + outputArff + " -d " + outputClassifier
                + " -L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a")
        MultilayerPerceptron.main(arguments.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
        val words = trainResult.getWords()
        val classifier: Classifier = ClassifyUtils.loadClassifier(outputClassifier)
        val model =
            Model(categories.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray(), words, classifier)
        SerializationUtils.dumpObject(outputModel, model)
        println("model saved as $outputModel")
    }
}

fun main() {

}
