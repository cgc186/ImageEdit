package opencv.bow

import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.ml.SVM
import java.io.File
import javax.imageio.ImageIO
import org.opencv.core.Mat
import org.opencv.features2d.*
import org.opencv.imgproc.Imgproc
import java.io.IOException
import org.opencv.imgcodecs.Imgcodecs.imwrite
import org.opencv.highgui.HighGui.*
import org.opencv.imgcodecs.Imgcodecs.imread
import java.awt.image.BufferedImage


class Features {

    var DATA_FOLDER = "D:/project data/data/"
    var TEMPLATE_FOLDER = "D:/project data/data/templates/"

    var categories: Array<String> = arrayOf("Phoning", "PlayingGuitar")

    val TRAIN_FOLDER = "D:/project data/data/train_images/"
    val TEST_FOLDER = "D:/project data/data/test_image"
    val RESULT_FOLDER = "D:/project data/data/result_image/"

    private var categoriesSize = 0
    //从类目名称到训练图集的映射，关键字可以重复出现
    private var trainSet = mutableMapOf<String, MutableSet<Mat>>()

    private val bowtrainer = BOWKMeansTrainer(categoriesSize)

    //存放所有训练图片的BOW
    private val allsamplesBow = mutableMapOf<String, Mat>()

    //类目名称，也就是TRAIN_FOLDER设置的目录名
    private var categoryName = mutableListOf<String>()

    //特征检测器detectors与描述子提取器extractors
    private var featureDetector = FeatureDetector.create(categoriesSize)
    private val descriptorExtractor = DescriptorExtractor.create(categoriesSize)
    var bowDescriptorExtractor: BOWImgDescriptorExtractor? = null
    var storSvms: SVM? = null

    private var resultObjects: MutableMap<String, Mat>? = null

    //存放训练图片词典
    val vocab = Mat()

    // 构造函数
    constructor(categoriesSize: Int) {
        println("开始初始化...")
        this.categoriesSize = categoriesSize
        //从类目名称到数据的map映射
        var resultObjects = mutableMapOf<String, Mat>()
        // 训练得到的SVM
        storSvms = SVM.create()
        //var descriptorMacher = FlannBasedMatcher()
        bowDescriptorExtractor = BOWImgDescriptorExtractor.__fromPtr__(1L)

        val dir = File(TEMPLATE_FOLDER)
        val files = dir.listFiles()
        for (i in files.indices) {
            //println(files[i])
            if (!files[i].isDirectory
                && files[i].name.contains(".jpg")
            ) {
                val temp = imread(files[i].absolutePath)

            }
        }
    }

    fun removeExtention(full_name: String) {

    }

    //构造训练集合
    fun makeTrainSet() {
        println("读取训练集...")
        val categor = ""
        val samples = ""
        //类别
        categories?.forEach { cate ->
            categoryName.add(cate)
            val dir = File(TRAIN_FOLDER + File.separator + cate)
            val files = dir.listFiles() ?: throw  IOException("cannot find category $cate")
            for (i in files.indices) {
                //println(files[i])
                if (!files[i].isDirectory
                    && files[i].name.contains(".jpg")
                ) {
                    val temp = imread(files[i].absolutePath)
                    trainSet[cate]!!.add(temp)
                }
            }
        }
        categoriesSize = categoryName.size
        println("发现${categoriesSize}种类别物体...")
    }

    // 聚类得出词典
    fun bulidVacab() {
        val image = Imgcodecs.imread("")

        var vocabDescriptors: Mat = Mat()
        val kp = MatOfKeyPoint()

        //var featureDetector = FeatureDetector.create(categoriesSize)
        trainSet.forEach { matList ->
            matList.value.forEach {
                var descriptors: Mat = Mat()
                featureDetector.detect(it, kp)
                descriptorExtractor.compute(it, kp, descriptors)
                vocabDescriptors.push_back(descriptors)
            }
        }
        bowtrainer.add(vocabDescriptors)
        val vocab = bowtrainer.cluster()
        val img = Mat2BufImg.Mat2BufImg(vocab, ".jpg")
        ImageIO.write(img, "jpg", File(DATA_FOLDER + "vocab.jpg"))
    }

    //构造BOW
    fun computeBowImage() {
//        val doc = DocumentBuilderFactory.newInstance()
//        val builder = doc.newDocumentBuilder()
//        val xml = builder.parse("$DATA_FOLDER/vocab.xml")
//
//        val vocabulary = xml.getElementsByTagName("vocabulary")
//        val mat = Mat(vocabulary)

        val img = ImageIO.read(File(DATA_FOLDER + "vocab.jpg"))

        val tempVocabulary = Mat2BufImg.BufImg2Mat(img, BufferedImage.TYPE_3BYTE_BGR, CvType.CV_8UC3)
        if (tempVocabulary != null) {
            bowDescriptorExtractor!!.vocabulary = tempVocabulary

        } else {
            bowDescriptorExtractor!!.vocabulary = vocab
        }

        var bowPath = DATA_FOLDER + "bow.txt"
        val file = File(bowPath)
        if (file.exists()) {
            println("BOW 已经准备好...")
        } else {
            val kp = MatOfKeyPoint()
            trainSet.forEach { matList ->
                val cateName = matList.key
                matList.value.forEach {
                    val tempImage = it
                    var imageDescriptor = Mat()
                    featureDetector.detect(tempImage, kp)
                    bowDescriptorExtractor!!.compute(tempImage, kp, imageDescriptor)
                    allsamplesBow[cateName]!!.push_back(imageDescriptor)
                }
            }
        }
        file.writeText("flag")
        println("bag of words构造完毕...")
    }

    //训练分类器
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
            val svmFilename = DATA_FOLDER + categoryName[i] + "SVM.xml"
            storSvms!!.save(svmFilename)
        }
    }

    //将测试图片分类
    fun categoryBySvm() {
        println("物体分类开始..")
        val grayPic = Mat()
        val thresholdImage = Mat()
        var predictionCategory = ""

        var curConfidence = 0.0f

        val dir = File(TEST_FOLDER)

        val files = dir.listFiles() ?: throw  IOException("cannot find category ")
        for (i in files.indices) {
            //获取该目录下的图片名
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
                bowDescriptorExtractor!!.compute(grayPic, kp, test)

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
