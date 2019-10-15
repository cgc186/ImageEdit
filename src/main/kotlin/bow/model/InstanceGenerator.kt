package bow.model

import bow.cluster.KMeansCluster
import bow.feature.Sift
import java.io.*

class InstanceGenerator {

    /* 词典大小, 也是 K-Means 聚类得到的类别数量 */
    private val PARTITION = 80

    private val featureMaker = Sift()

    private val cluster = KMeansCluster()

    private var categories: Array<String>? = null

    constructor()

    constructor(categories: Array<String>) {
        this.categories = categories
    }

    /**
     * 得到样本文件列表
     */
    fun getSamples(base: String, start: Int, end: Int): ArrayList<Sample> {
        val samples = java.util.ArrayList<Sample>()
        categories?.forEach { cate ->
            var count = 0
            val dir = File(base + File.separator + cate)
            val files = dir.listFiles() ?: throw IOException("cannot find category $cate")
            for (i in files.indices) {
                if (!files[i].isDirectory
                    && files[i].name.contains(".jpg")
                ) {
                    if (count > start) {
                        samples.add(Sample(files[i].absolutePath, cate))
                    }
                    if (count >= end) {
                        break
                    }
                }
            }
        }
        return samples
    }

    /**
     * 根据样本文件列表生成特征点向量的列表*
     * @param samples
     * @return
     * @throws IOException
     */
    fun getFeatures(samples: ArrayList<Sample>): MutableMap<String, MutableList<Feature>> {
        val bow = ArrayList<Feature>()
        val allFeatures = mutableMapOf<String, MutableList<Feature>>()
        samples.forEach { sample ->
            val key: String = sample.getPath().toString()
            println("generating feature from $key")
            try {
                val features = featureMaker.getFeatures(key)
                if (features == null || features.size == 0) {
                    throw Exception("no feature found")
                }
                bow.addAll(features)
                allFeatures[key] = features
            } catch (e: Exception) {
                System.err.println(
                    "failed to sample " + key + ": " + e.message
                )
            }
        }
        allFeatures["bow"] = bow
        return allFeatures
    }

    /**
     * 根据特征点向量的词袋生成词典
     *
     * @param bow
     * @return
     */
    private fun calcDict(bow: MutableList<Feature>): MutableList<Feature>? {
        println("bow size: " + bow.size)
        val clusterResult = cluster.getSets(bow, PARTITION)
        return clusterResult.getCentroids()
    }

    /**
     * 根据词典和特征点向量的列表生成样本描述 (未标注)
     *
     * @param features
     * @param dict
     * @return
     */
    fun getInstance(features: MutableList<Feature>, dict: MutableList<Feature>): Instance {
        val dictSize = dict.size
        var counts = IntArray(dictSize)
        for (i in features.indices) {
            var nearest = -1
            var nearestDist = java.lang.Double.MAX_VALUE
            for (j in 0 until dictSize) {
                var dist = dict[j].distance(features[i])
                if (dist < nearestDist) {
                    nearestDist = dist
                    nearest = j
                }
            }
            counts[nearest]++
        }
        var freq = DoubleArray(dictSize)
        for (i in 0 until dictSize) {
            freq[i] = (counts[i] / features.size).toDouble()
        }
        return Instance(freq)
    }

    /**
     * 给定图像目录生成样本类表和词典
     *
     * @param imgBase
     * @param cateSample
     * @return
     * @throws Exception
     */
    fun train(imgBase: String, cateSample: Int): TrainResult {
        var samples = getSamples(imgBase, 0, cateSample)
        var allFeatures = getFeatures(samples)
        var bow = allFeatures["bow"]
        var dict = bow?.let { calcDict(it) }
        var instances = ArrayList<Instance>()
        samples.forEach { sample ->
            var features = allFeatures[sample.getPath()]
            if (features == null) {

            }
        }
        for (sample in samples) {
            val features = allFeatures[sample.getPath()] ?: continue
            val instance = getInstance(features, dict!!)
            sample.getPath()?.let { instance.setImage(it) }
            sample.getCategory()?.let { instance.setCategory(it) }
            instances.add(instance)
        }
        return TrainResult(instances, dict)
    }

    fun dumpArff(instances: ArrayList<Instance>, output: String) {
        val br = BufferedWriter(OutputStreamWriter(FileOutputStream(File(output))))
        br.write("@relation features\n")
        for (i in 0 until PARTITION) {
            br.write("@attribute dimension$i numeric\n")
        }
        br.write("@attribute class {")
        for (i in categories!!.indices) {
            if (i > 0) {
                br.write(",")
            }
            br.write(categories!![i])
        }
        br.write("}\n")
        br.write("@data\n")
        for (instance in instances) {
            var tmp = ""
            val freq = instance.getFreq()
            for (i in freq!!.indices) {
                tmp += freq[i].toString() + ","
            }
            tmp += instance.getCategory()
            tmp += "\n"
            br.write(tmp)
        }
        br.close()
    }
}