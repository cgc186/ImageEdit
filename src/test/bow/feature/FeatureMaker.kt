package bow.feature

import bow.model.Feature
import java.io.IOException
/**
 * 特征提取器接口描述
 */
interface FeatureMaker {
    /* generate features from specified img */
    @Throws(IOException::class)
    abstract fun getFeatures(img: String): List<Feature>

}