package bow.cluster

import bow.model.Feature
import bow.util.FeatureTree
import kotlin.collections.ArrayList



class KMeansCluster : Cluster {
    /* 误差阈值 */
    private val ERROR_THRESHOLD = 1.2f

    override fun getSets(features: List<Feature>, partition: Int): ClusterResult {
        var centroids = mutableListOf<Feature>()
        val belongs = IntArray(features.size)

        val random = java.util.Random()

        val pool = ArrayList(features)
        for (i in 0 until partition) {
            val index = random.nextInt(pool.size)
            centroids.add(pool[index])
            pool.removeAt(index)
        }
        var index = 1
        while (true) {
            println(index)
            index++
            val elements = mutableMapOf<Int, MutableList<Feature>>()
            for (i in 0 until partition) {
                elements[i] = mutableListOf<Feature>()
            }
            val tree = FeatureTree()
            for (i in 0 until centroids.size) {
                tree.add(i, centroids[i])
            }
            for (i in features.indices) {
                val f = features[i]
                belongs[i] = tree.queryNearest(f)
                elements[belongs[i]]?.add(f)
            }
            var error = 0.0
            elements.forEach {
                val set = it.key
                val newCentroid = Feature(false)

                for (feature in it.value) {
                    newCentroid.add(feature)
                }
                newCentroid.divide(it.value.size)
                error += newCentroid.distance(centroids[set])
                centroids[set] = newCentroid
            }
            error /= partition
            println("""K-Means error: $error""")
            if (error < ERROR_THRESHOLD){
                break
            }
        }
        return ClusterResult(centroids, belongs)
    }
}