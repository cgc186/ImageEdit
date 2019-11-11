package bow.cluster

import bow.model.Feature

interface Cluster {
    /* run clustering in features */
    abstract fun getSets(features: List<Feature>, partition: Int): ClusterResult
}