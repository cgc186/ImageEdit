package lda

//import org.opencv.core.Core
//import org.opencv.core.Core.NORM_MINMAX
//import org.opencv.core.Core.normalize
//import org.opencv.core.CvType.CV_8UC1
//import org.opencv.core.CvType.CV_8UC3
//import org.opencv.core.Mat
//import org.opencv.face.FisherFaceRecognizer
//import org.opencv.imgcodecs.Imgcodecs.imread
//import org.bytedeco.javacpp.opencv_core
//import org.bytedeco.javacpp.opencv_core.*
//import org.bytedeco.javacpp.opencv_face.FisherFaceRecognizer
//import org.bytedeco.javacpp.opencv_imgcodecs.imread

//import org.bytedeco.javacpp.DoublePointer
//import org.bytedeco.javacpp.IntPointer
//import org.bytedeco.opencv.global.opencv_core.*
//import org.bytedeco.opencv.global.opencv_imgcodecs.imread
//import org.bytedeco.opencv.opencv_core.Mat
//import org.bytedeco.opencv.opencv_core.MatVector
//import org.bytedeco.opencv.opencv_face.FisherFaceRecognizer
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

object Lda {
    fun getFileList(
        filename: String,
        index: Int,
        imgTrainListPath: String,
        testFilePath: String,
        templatesFilePath: String
    ) {
        val imgList = java.util.ArrayList<String>()
        val templates = java.util.ArrayList<String>()
        val test = java.util.ArrayList<String>()

        val files = File(filename)
        val list = files.list()
        list?.forEach {
            val img = File("$filename/$it")
            val listFiles = img.listFiles()
            templates.add(listFiles[0].absolutePath + ";" + it)
            var size = listFiles.size
            for (i in 0 until size - index) {
                imgList.add(listFiles[i].absolutePath + ";" + it)
            }
            if (index != 0) {
                for (i in size - index until size) {
                    println(listFiles[i].absolutePath)
                    test.add(listFiles[i].absolutePath + ";" + it)
                }
            }
        }
        try {
            val fileWriter1 = FileWriter(imgTrainListPath)
            val bw1 = BufferedWriter(fileWriter1)
            for (file in imgList) {
                bw1.write(file + "\n")
            }
            bw1.close()
            fileWriter1.close()
            val fileWriter2 = FileWriter(templatesFilePath)
            val bw2 = BufferedWriter(fileWriter2)
            for (file in templates) {
                bw2.write(file + "\n")
            }
            bw2.close()
            fileWriter2.close()
            if (index != 0) {
                val fileWriter3 = FileWriter(testFilePath)
                val bw3 = BufferedWriter(fileWriter3)
                for (file in test) {
                    bw3.write(file + "\n")
                }
                bw3.close()
                fileWriter3.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    fun norm0255(src: File): Mat {
//        var mat = imread(src.absolutePath)
//        var dst = Mat()
//        when (mat.channels()) {
//            1 -> normalize(mat, dst, 0.0, 255.0, NORM_MINMAX, CV_8UC1, Mat())
//            3 -> normalize(mat, dst, 0.0, 255.0, NORM_MINMAX, CV_8UC3, Mat())
//            else -> dst = mat
//        }
//        return dst
//    }
//
    fun removeExtention(full_name: String, separator: String): List<String> {
        var lastIndex = full_name.split(separator);
        return lastIndex
    }

//    fun readTrain(filename: String, images: MatVector, labels: ArrayList<Int>) {
//        val file = File(filename)
//        for (image in file.readLines()) {
//            val imagePath = removeExtention(image, ";")
//            images.push_back(imread(imagePath[0]))
//            labels.add(imagePath[1].toInt())
//        }
//    }
//
//    fun readCsv(filename: String, images: ArrayList<Mat>, labels: ArrayList<Int>) {
//        val file = File(filename)
//        for (image in file.readLines()) {
//            val imagePath = removeExtention(image, ";")
//            images.add(imread(imagePath[0]))
//            labels.add(imagePath[1].toInt())
//        }
//    }
//
//    fun train() {
//        val trainImages = MatVector()
//
//        val file = File("data/imgTrainList.txt")
//        val listFiles = file.listFiles()
//        for (image in file.readLines()) {
//            val imagePath = removeExtention(image, ";")
//            trainImages.push_back(imread(imagePath[0]))
//        }
//        val labels = Mat(320, 1, CV_32SC1)
//        val model = FisherFaceRecognizer.create()
//        model.train(trainImages, labels)
//        model.save("data/MyFaceFisherModel.xml")
//    }
//
//    fun test() {
//        var testImages = ArrayList<Mat>()
//        var testLabels = ArrayList<Int>()
//        readCsv("data/test.txt", testImages, testLabels)
//        val model = FisherFaceRecognizer.create()
//        model.read("data/MyFaceFisherModel.xml")
//        testImages.forEach {
//            var predictedLabel = IntPointer()
//            var confidence = DoublePointer()
//            model.predict(it, predictedLabel, confidence)
//            println(predictedLabel)
//        }
//    }
}

