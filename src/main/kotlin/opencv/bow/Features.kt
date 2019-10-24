package opencv.bow

import org.opencv.core.*
import org.opencv.features2d.BOWKMeansTrainer
import org.opencv.features2d.DescriptorExtractor
import org.opencv.features2d.FeatureDetector
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.ml.SVM
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import org.opencv.core.Mat




class Features {

    var categoriesSize = 11

    var trainSet = mutableMapOf<String, Mat>()

    val bowtrainer = BOWKMeansTrainer(categoriesSize)

    //存放所有训练图片的BOW
    val allsamples_bow = mutableMapOf<String, Mat>()

    //类目名称，也就是TRAIN_FOLDER设置的目录名
    var category_name = mutableListOf<String>()

    fun bulidVacab() {
        val image = Imgcodecs.imread("")

        var vocabDescriptors: Mat = Mat()
        val kp = MatOfKeyPoint()

        var featureDetector = FeatureDetector.create(categoriesSize)
        val descriptorExtractor = DescriptorExtractor.create(categoriesSize)


        trainSet.forEach {
            var cateName = it.key
            var descriptors: Mat = Mat()
            featureDetector.detect(it.value, kp)
            descriptorExtractor.compute(it.value, kp, descriptors)
            vocabDescriptors.push_back(descriptors)
        }
        bowtrainer.add(vocabDescriptors)
        val vocab = bowtrainer.cluster()
        val img = Mat2BufImg.Mat2BufImg(vocab, ".jpg")
        ImageIO.write(img, "jpg", File(""))
    }

    fun computeBowImage() {
        val img = ImageIO.read(File(""))
        Mat2BufImg.BufImg2Mat(img, BufferedImage.TYPE_3BYTE_BGR, CvType.CV_8UC3)
    }

    fun trainSvm() {
        var storSvms = SVM.create()
        //设置训练参数
        storSvms.type = SVM.C_SVC
        storSvms.setKernel(SVM.LINEAR)
        storSvms.termCriteria = TermCriteria(1, 100, 1e-6)
        for (i in 0 until categoriesSize) {
            var temSamples = Mat(
                0,
                allsamples_bow[category_name[i]]!!.cols(),
                allsamples_bow[category_name[i]]!!.type()
            )
            var responses = Mat(0, 1, CvType.CV_32SC1)
            temSamples.push_back(allsamples_bow[category_name[i]])
            var posResponses = Mat(
                allsamples_bow[category_name[i]]!!.rows(),
                1,
                CvType.CV_32SC1,
                Scalar.all(1.0))
            responses.push_back(posResponses)

            allsamples_bow.forEach{
                if (it.key == category_name[i]){
                    return@forEach
                }
                temSamples.push_back(it.value)
                val response = Mat( it.value.rows(), 1, CvType.CV_32SC1, Scalar.all(-1.0) )
                responses.push_back( response )
            }

            storSvms.train(temSamples,i,responses)
            val svmFilename = "D:/project data/data/" + category_name[i] + "SVM.xml"
            storSvms.save(svmFilename)
        }
    }

    fun categoryBySvm(){
        println("物体分类开始..")
        val grayPic = Mat()
        val thresholdImage = Mat()
        var predictionCategory = ""
        var curConfidence = 0.0f
        val TEST_FOLDER = ""

    }
}

