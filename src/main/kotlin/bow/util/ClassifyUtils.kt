package bow.util

import bow.model.Instance

import weka.classifiers.Classifier
import weka.core.SparseInstance

class ClassifyUtils {
    @Throws(Exception::class)
    fun loadClassifier(input: String): Classifier {
        return SerializationUtils.loadObject(input) as Classifier
    }

    fun getWekaInstance(instance: Instance): weka.core.Instance {
        return SparseInstance(0.0, instance.getFreq())
    }
}