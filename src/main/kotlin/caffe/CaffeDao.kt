package caffe

import caffejni.Caffe

object CaffeDao {
    private var caffe = Caffe()
    private const val modelFile = "Z:/MobileNetSSD_deploy.caffemodel"
    private const val modelTextFile = "Z:/MobileNetSSD_deploy.prototxt"

    private const val modelTxt = "Z:/bvlc_googlenet.prototxt"
    private const val modelBin = "Z:/bvlc_googlenet.caffemodel"
    private const val synSetWords = "Z:/synset_words.txt"

    fun caffe(imagePath: String, savePath: String) {
//        val imagePath = "Z:/rgb.jpg"
//        val savePath = "D:/out.png"
        caffe.caffe(modelFile, modelTextFile, imagePath, savePath)
    }

    fun getItemType(imagePath:String): String? {
        return caffe.getType(modelTxt,modelBin,synSetWords,imagePath)
    }
}