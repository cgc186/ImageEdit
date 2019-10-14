package bow.feature

import bow.model.Feature
import java.io.IOException

interface FeatureMaker {
    /* generate features from specified img */
    @Throws(IOException::class)
    abstract fun getFeatures(img: String): List<Feature>

}