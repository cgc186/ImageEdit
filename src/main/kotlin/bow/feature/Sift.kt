package bow.feature

import bow.model.Feature
import com.alibaba.simpleimage.analyze.sift.IPixelConverter
import com.alibaba.simpleimage.analyze.sift.SIFT
import com.alibaba.simpleimage.analyze.sift.render.RenderImage
import java.awt.image.BufferedImage
import java.io.File
import java.util.ArrayList
import javax.imageio.ImageIO
/**
 * SIFT特征提取实现
 */
class Sift {

    private fun getPixelConverter(): IPixelConverter {
        return IPixelConverter { r, g, b -> (r + g + b).toFloat() / 255f / 3f }
    }

    fun getFeature(img: String): ArrayList<Feature> {
        val `in` = ImageIO.read(File(img))
        val rImag = RenderImage(`in`)

        val pixels = rImag.toPixelFloatArray(getPixelConverter())
        val sift = SIFT()

        sift.detectFeatures(pixels)

        var features = ArrayList<Feature>()

        for (point in sift.globalKDFeaturePoints) {
            val feature = Feature(point)
            features.add(feature)
        }
        return features
    }

}