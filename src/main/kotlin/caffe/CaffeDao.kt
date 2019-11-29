package caffe

import caffejni.Caffe

object CaffeDao {
    private var caffe = Caffe()
    private const val modelFile = "D:/tmp/MobileNetSSD_deploy.caffemodel"
    private const val modelTextFile = "D:/tmp/MobileNetSSD_deploy.prototxt"

    private const val modelTxt = "D:/tmp/bvlc_googlenet.prototxt"
    private const val modelBin = "D:/tmp/bvlc_googlenet.caffemodel"
    private const val synSetWords = "D:/tmp/synset_words.txt"

    fun caffe(imagePath: String, savePath: String) {
//        val imagePath = "Z:/rgb.jpg"
//        val savePath = "D:/out.png"
        caffe.caffe(modelFile, modelTextFile, imagePath, savePath)
    }

    fun getItemType(imagePath:String): String? {
        return caffe.getType(modelTxt,modelBin,synSetWords,imagePath)
    }
}