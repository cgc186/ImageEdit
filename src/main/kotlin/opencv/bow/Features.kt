package opencv.bow

import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.ml.SVM
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.io.IOException
import org.opencv.features2d.*
import org.opencv.imgcodecs.Imgcodecs.imwrite
import org.opencv.highgui.HighGui.*


class Features {


    private var categoriesSize = 0

    private var trainSet = mutableMapOf<String, Mat>()

    private val bowtrainer = BOWKMeansTrainer(categoriesSize)

    //存放所有训练图片的BOW
    private val allsamplesBow = mutableMapOf<String, Mat>()

    //类目名称，也就是TRAIN_FOLDER设置的目录名
    private var categoryName = mutableListOf<String>()


    private var featureDetector = FeatureDetector.create(categoriesSize)
    private val descriptorExtractor = DescriptorExtractor.create(categoriesSize)

    var storSvms: SVM? = null

    private var resultObjects: MutableMap<String, Mat>? = null

    init {

        //从类目名称到数据的map映射
        var resultObjects = mutableMapOf<String, Mat>()
        // 训练得到的SVM
        var storSvms = SVM.create()

        var descriptorMacher = FlannBasedMatcher()
        //var bowDescriptorExtractor = BOWImgDescriptorExtractor()
    }

    constructor(categoriesSize: Int) {
        this.categoriesSize = categoriesSize
    }

    fun bulidVacab() {
        val image = Imgcodecs.imread("")

        var vocabDescriptors: Mat = Mat()
        val kp = MatOfKeyPoint()

        //var featureDetector = FeatureDetector.create(categoriesSize)
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
        storSvms = SVM.create()
        //设置训练参数
        storSvms!!.type = SVM.C_SVC
        storSvms!!.setKernel(SVM.LINEAR)
        storSvms!!.termCriteria = TermCriteria(1, 100, 1e-6)
        for (i in 0 until categoriesSize) {
            var temSamples = Mat(
                0,
                allsamplesBow[categoryName[i]]!!.cols(),
                allsamplesBow[categoryName[i]]!!.type()
            )
            var responses = Mat(0, 1, CvType.CV_32SC1)
            temSamples.push_back(allsamplesBow[categoryName[i]])
            var posResponses = Mat(
                allsamplesBow[categoryName[i]]!!.rows(),
                1,
                CvType.CV_32SC1,
                Scalar.all(1.0)
            )
            responses.push_back(posResponses)

            allsamplesBow.forEach {
                if (it.key == categoryName[i]) {
                    return@forEach
                }
                temSamples.push_back(it.value)
                val response = Mat(it.value.rows(), 1, CvType.CV_32SC1, Scalar.all(-1.0))
                responses.push_back(response)
            }

            storSvms!!.train(temSamples, i, responses)
            val svmFilename = "D:/project data/data/" + categoryName[i] + "SVM.xml"
            storSvms!!.save(svmFilename)
        }
    }

    fun categoryBySvm() {
        println("物体分类开始..")
        val grayPic = Mat()
        val thresholdImage = Mat()
        var predictionCategory = ""

        var curConfidence = 0.0f

        val TEST_FOLDER = ""

        val dir = File(TEST_FOLDER)

        val files = dir.listFiles() ?: throw  IOException("cannot find category ")
        for (i in files.indices) {
            if (!files[i].isDirectory
                && files[i].name.contains(".jpg")
            ) {
                println("${files[i]}")

                var trainPicName = files[i].toString()

                //读取图片
                var inputPic = Imgcodecs.imread(files[i].toString())

                Imgproc.cvtColor(inputPic, grayPic, Imgproc.COLOR_BGR2GRAY)

                val kp = MatOfKeyPoint()
                var test = Mat()
                featureDetector.detect(grayPic, kp)
                //bowDescriptorExtractor.

                var sign = 0
                var bestScore = -2.0f

                for (i in 0 until categoriesSize) {
                    val cateName = categoryName[i]
                    val f_path = "D:/project data/data/" + cateName + "SVM.xml"
                    //FileStorage svm_fs(f_path,FileStorage::READ);
                    val svmFile = ImageIO.read(File(f_path))
                    if (svmFile != null) {
                        var stSvm = SVM.load(f_path)
                        if (sign == 0) {
                            //val scoreValue = stSvm.predict(test, true)
                            //var classValue = stSvm.predict(test, false)
                            //sign = if (scoreValue < 0.0f == classValue < 0.0f) 1 else -1
                            val value = stSvm.predict(test)
                            sign = if (value < 0.0f) 1 else -1
                        }
                        //curConfidence = sign * stSvm.predict(test, true);
                        curConfidence = sign * stSvm.predict(test);
                    } else {
                        if (sign == 0) {
//                            val scoreValue = storSvms.predict(test, true)
//                            val classValue = storSvms.predict(test, false)
//                            sign = if (scoreValue < 0.0f == classValue < 0.0f) 1 else -1
                            val value = storSvms!!.predict(test)
                            sign = if (value < 0.0f) 1 else -1
                        }
                        curConfidence = sign * storSvms!!.predict(test);
                    }
                    if (curConfidence > bestScore) {
                        bestScore = curConfidence
                        predictionCategory = cateName
                    }
                }
                var RESULT_FOLDER = "D:/project data/data/result_image/"

                val files = dir.listFiles() ?: throw  IOException("cannot find category ")
                for (i in files.indices) {
                    //println(files[i])
                    if (!files[i].isDirectory
                        && files[i].name == predictionCategory
                    ) {
                        val filename = "$RESULT_FOLDER$predictionCategory/$trainPicName"
                        imwrite(filename, inputPic)
                    }
                }
                //显示输出
                namedWindow("Dectect Object")
                println("这张图属于：$predictionCategory")
                imshow("Dectect Object", resultObjects?.get(predictionCategory))
                waitKey(0)
            }
        }
    }
}
