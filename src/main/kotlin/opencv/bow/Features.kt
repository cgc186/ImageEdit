package opencv.bow


//import org.bytedeco.opencv.global.opencv_core.noArray
//import org.bytedeco.opencv.global.opencv_highgui.*
//import org.bytedeco.opencv.global.opencv_imgcodecs.imread
//import org.bytedeco.opencv.global.opencv_imgcodecs.imwrite
//import org.bytedeco.opencv.global.opencv_imgproc.cvtColor
//import org.bytedeco.opencv.opencv_core.*
//import org.bytedeco.opencv.opencv_features2d.*
//import org.bytedeco.opencv.opencv_ml.SVM
import org.bytedeco.javacpp.opencv_core.*
import org.bytedeco.javacpp.opencv_features2d.*
import org.bytedeco.javacpp.opencv_highgui.*
import org.bytedeco.javacpp.opencv_imgcodecs.*
import org.bytedeco.javacpp.opencv_imgproc.COLOR_BGR2GRAY
import org.bytedeco.javacpp.opencv_imgproc.cvtColor
import org.bytedeco.javacpp.opencv_ml.*
import java.io.File
import java.io.IOException

class Features {

    var DATA_FOLDER = "D:/project data/data/"
    var TEMPLATE_FOLDER = "D:/project data/data/templates/"

    var categories: Array<String> = arrayOf("airplanes", "butterfly", "camera", "scissors", "sunflower")

    val TRAIN_FOLDER = "D:/project data/data/train_images/"
    val TEST_FOLDER = "D:/project data/data/test_image"
    val RESULT_FOLDER = "D:/project data/data/result_image/"

    //类目数目
    private var categoriesSize = 0
    //从类目名称到训练图集的映射，关键字可以重复出现
    private var trainSet = mutableMapOf<String, MutableSet<Mat>>()

    private var bowtrainer: BOWKMeansTrainer? = null

    //存放所有训练图片的BOW
    private val allsamplesBow = mutableMapOf<String, Mat>()

    //类目名称，也就是TRAIN_FOLDER设置的目录名
    private var categoryName = mutableListOf<String>()

    //特征检测器detectors与描述子提取器extractors
    private var featureDetector: FastFeatureDetector? = null
    private var descriptorExtractor: FastFeatureDetector? = null
    private var descriptorMacher: DescriptorMatcher? = null

    var bowDescriptorExtractor: BOWImgDescriptorExtractor? = null
    var storSvms: SVM? = null

    private var resultObjects: MutableMap<String, Mat>? = null

    //存放训练图片词典
    private val vocab = Mat()

    var clusters: Int = 0

    // 构造函数
    constructor(clusters: Int) {
        println("开始初始化...")
        this.clusters = clusters
        //从类目名称到数据的map映射
        var resultObjects = mutableMapOf<String, Mat>()
        // 训练得到的SVM
        storSvms = SVM.create()

        featureDetector = FastFeatureDetector.create()

        bowtrainer = BOWKMeansTrainer(clusters)

        descriptorExtractor = FastFeatureDetector.create()
        //var descriptorMacher = FlannBasedMatcher()
        descriptorMacher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE)
        bowDescriptorExtractor =BOWImgDescriptorExtractor(descriptorExtractor, descriptorMacher)

        //获取该目录下的所有文件名
        val dir = File(TEMPLATE_FOLDER)
        val files = dir.listFiles()
        for (i in files.indices) {
            //println(files[i])
            if (!files[i].isDirectory
                && files[i].name.contains(".jpg")
            ) {
                val subCategory = removeExtention(files[i].absolutePath)
                val image = imread(files[i].absolutePath)
                //存储原图模板
                resultObjects[subCategory] = image
            }
        }
        println("初始化完毕...")
        makeTrainSet()
    }

    private fun removeExtention(full_name: String): String {
        var lastIndex = full_name.split(".");
        return lastIndex[0]
    }

    //构造训练集合
    private fun makeTrainSet() {
        println("读取训练集...")
        //类别
        categories.forEach { cate ->
            categoryName.add(cate)
            var train = mutableSetOf<Mat>()
            trainSet[cate] = train
            val dir = File(TRAIN_FOLDER + File.separator + cate)
            val files = dir.listFiles() ?: throw  IOException("cannot find category $cate")
            for (i in files.indices) {
                //println(files[i])
                if (!files[i].isDirectory
                    && files[i].name.contains(".jpg")
                ) {
                    //println(files[i].absolutePath)
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
        val vacabFs = FileStorage(DATA_FOLDER + "vocab.xml",FileStorage.READ)

        if (vacabFs.isOpened) {
            println("图片已经聚类，词典已经存在..")
        } else {
            var vocabDescriptors = Mat()
            val kp = KeyPointVector()

            trainSet.forEach { matList ->
                matList.value.forEach {
                    var descriptors = Mat()
                    featureDetector!!.detect(it, kp)
                    bowDescriptorExtractor!!.compute(it, kp, descriptors)
                    val t = Mat(descriptors)
                    vocabDescriptors.push_back(t)
                }
            }
            bowtrainer!!.add(vocabDescriptors)
            val vocab = bowtrainer!!.cluster()
            val fileStorage = FileStorage(DATA_FOLDER + "vocab.xml", FileStorage.WRITE)
            fileStorage.write("vocabulary", vocab)
        }
    }

    //构造BOW
    fun computeBowImage() {
        val tempVocabulary = FileStorage(DATA_FOLDER + "vocab.xml", FileStorage.READ)

        if (tempVocabulary.isOpened) {
            var tempVacab = Mat()
            tempVacab = tempVocabulary["vocabulary"].mat()
            bowDescriptorExtractor!!.vocabulary = tempVacab
        } else {
            bowDescriptorExtractor!!.vocabulary = vocab
        }

        var bowPath = DATA_FOLDER + "bow.txt"
        val file = File(bowPath)
        if (file.exists()) {
            println("BOW 已经准备好...")
        } else {
            val kp = KeyPointVector()
            trainSet.forEach { matList ->
                val cateName = matList.key
                allsamplesBow[cateName] = Mat()
                matList.value.forEach {
                    val tempImage = it
                    var imageDescriptor = Mat()
                    featureDetector!!.detect(tempImage, kp)
                    bowDescriptorExtractor!!.compute(tempImage, kp, imageDescriptor)
                    val im = Mat(imageDescriptor)
                    allsamplesBow[cateName]!!.push_back(im)
                }
            }
        }
        file.writeText("flag")
        println("bag of words构造完毕...")
    }

    //训练分类器
    fun trainSvm() {
        var flag = 0
        for (k in 0 until categoriesSize) {
            val svmFil = FileStorage(DATA_FOLDER + categoryName[k] + "SVM.xml", FileStorage.READ)
            if (svmFil.isOpened) {
                continue
            } else {
                flag = -1
                break
            }
        }

        if (flag != -1) {
            println("分类器已经训练完毕...")
        } else {
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
                var responses = Mat(0, 1, CV_32SC1)
                temSamples.push_back(allsamplesBow[categoryName[i]])
                var posResponses = Mat(
                    allsamplesBow[categoryName[i]]!!.rows(),
                    1,
                    CV_32SC1,
                    Scalar.all(1.0)
                )
                responses.push_back(posResponses)

                allsamplesBow.forEach {
                    if (it.key == categoryName[i]) {
                        return@forEach
                    }
                    temSamples.push_back(it.value)
                    val response = Mat(it.value.rows(), 1, CV_32SC1, Scalar.all(-1.0))
                    responses.push_back(response)
                }

                storSvms!!.train(temSamples, i, responses)
                val svmFilename = DATA_FOLDER + categoryName[i] + "SVM.xml"
                storSvms!!.save(svmFilename)
            }
        }
    }

    //将测试图片分类
    fun categoryBySvm() {
        println("物体分类开始..")
        val grayPic = Mat()
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
                var inputPic = imread(files[i].toString())
                cvtColor(inputPic, grayPic, COLOR_BGR2GRAY)

                // 提取BOW描述子
                val kp = KeyPointVector()
                var test = Mat()
                featureDetector!!.detect(grayPic, kp)
                bowDescriptorExtractor!!.compute(grayPic, kp, test)
//                featureDetector!!.detectAndCompute(grayPic,noArray(),kp,test)

                var sign = 0
                var bestScore = -2.0f

                for (i in 0 until categoriesSize) {
                    val cateName = categoryName[i]
                    val fPath = DATA_FOLDER + cateName + "SVM.xml"
                    val svmFile = FileStorage(fPath, FileStorage.READ)
                    if (svmFile.isOpened) {
                        var stSvm = SVM.load(fPath)
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
