package opencv.bow

import org.opencv.core.KeyPoint
import org.opencv.core.Mat
import org.opencv.features2d.BFMatcher
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.Feature2D
import org.opencv.features2d.FeatureDetector
import org.opencv.features2d.FeatureDetector.SURF
import org.opencv.imgcodecs.Imgcodecs



class Features {
    fun features(){
        val image = Imgcodecs.imread("")

        var vocabDescriptors : Mat
        var kp = mutableListOf<KeyPoint>()
        val descriptors: Mat

        val featureDecter = SURF
        var detector = DescriptorMatcher.create("SIFT")
        val descriptorMacher = BFMatcher.create()
    }
}