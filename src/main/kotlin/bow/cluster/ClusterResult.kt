package bow.cluster

import bow.model.Feature

/**
 * 聚类结果描述
 */
class ClusterResult {
    var centroids: List<Feature>

    var belongs: IntArray

    constructor(centroids: MutableList<Feature>, belongs: IntArray) {
        this.centroids = centroids
        this.belongs = belongs
    }
}