package lda

import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.*


//-Djava.library.path=E:\lib\opencv\opencv3.4.1\build\java\x64;E:\lib\opencv\opencv3.4.1\build\x64\vc15

fun main() {
//    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
//    val mat = Mat.eye(3, 3, CvType.CV_8UC1)
//    println("mat = " + mat.dump())

    getFileList("E:\\coding\\c++\\lda\\FaceDB_orl", 2)
}

fun getFileList(filename: String, index: Int) {
    val imgList = ArrayList<String>()
    val templates = ArrayList<String>()
    val test = ArrayList<String>()

    val files = File(filename)
    val list = files.list()
    list.forEach {
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
        val fileWriter1 = FileWriter("data/imgTrainList.txt")
        val bw1 = BufferedWriter(fileWriter1)
        for (file in imgList) {
            bw1.write(file + "\n")
        }
        bw1.close()
        fileWriter1.close()
        val fileWriter2 = FileWriter("data/templates.txt")
        val bw2 = BufferedWriter(fileWriter2)
        for (file in templates) {
            bw2.write(file + "\n")
        }
        bw2.close()
        fileWriter2.close()
        if (index != 0){
            val fileWriter3 = FileWriter("data/test.txt")
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