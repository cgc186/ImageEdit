package bow.feature

import bow.model.Feature
import com.alibaba.simpleimage.analyze.sift.IPixelConverter
import com.alibaba.simpleimage.analyze.sift.SIFT
import com.alibaba.simpleimage.analyze.sift.render.RenderImage
import java.awt.image.BufferedImage
import java.util.ArrayList

class Sift {

    private fun getPixelConverter(): IPixelConverter {
        return IPixelConverter { r, g, b -> (r + g + b).toFloat() / 255f / 3f }
    }

    fun getFeatures(img: BufferedImage): ArrayList<Feature> {
        val rImag = RenderImage(img)

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

