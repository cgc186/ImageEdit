package bow.util

import bow.model.Feature
/**
 * 特征向量树 (KDTree)
 */
class FeatureTree :KDTree.Euclidean<Int>{
    constructor() : super(Feature.DIMENSION)

    private fun getValue(feature: Feature): DoubleArray {
        val location = DoubleArray(Feature.DIMENSION)
        val values = feature.values
        for (i in 0 until Feature.DIMENSION) {
            location[i] = values!![i].toDouble()
        }
        return location
    }

    fun add(payload: Int, feature: Feature) {
        this.addPoint(getValue(feature), payload)
    }

    fun queryNearest(feature: Feature): Int? {
        return this.nearestNeighbours(getValue(feature), 1)[0].payload
    }
}