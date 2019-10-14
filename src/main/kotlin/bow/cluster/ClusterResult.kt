package bow.cluster

import bow.model.Feature

/**
 * 聚类结果描述
 */
class ClusterResult {
    private var centroids: MutableList<Feature>? = null

    private var belongs: IntArray? = null

    constructor(centroids: MutableList<Feature>, belongs: List<IntArray>) {
        this.centroids = centroids
        this.belongs = belongs
    }

    /**
     * @return the centroids
     */
    fun getCentroids(): MutableList<Feature>? {
        return centroids
    }

    /**
     * @param centroids
     * the centroids to set
     */
    fun setCentroids(centroids: MutableList<Feature>) {
        this.centroids = centroids
    }

    /**
     * @return the belongs
     */
    fun getBelongs(): IntArray? {
        return belongs
    }

    /**
     * @param belongs
     * the belongs to set
     */
    fun setBelongs(belongs: IntArray) {
        this.belongs = belongs
    }
}