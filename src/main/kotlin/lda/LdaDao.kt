package lda

import ldajni.LdaFace
import java.io.File

object LdaDao {
    var faceModelPath = "data/ldaData/"
    var imgTrainPath = faceModelPath + "imgTrainList.txt"
    var testFile = faceModelPath + "test.txt"
    private var templates = faceModelPath + "templates.txt"

    var testList =
        mutableMapOf<String, Int>()

    var templatesList =
        mutableMapOf<Int, String>()

    private fun initTestList(path: String) {
        val file = File(path)
        for (image in file.readLines()) {
            val imagePath = Lda.removeExtention(image, ";")
            testList[imagePath[0]] = imagePath[1].toInt()
        }
    }

    private fun initList(path: String, list: MutableMap<Int, String>) {
        val file = File(path)
        for (image in file.readLines()) {
            val imagePath = Lda.removeExtention(image, ";")
            list[imagePath[1].toInt()] = imagePath[0]
        }
    }

    private val lf = LdaFace()

    fun initFolderList(filePath: String, index: Int) {
        Lda.getFileList(filePath, index, imgTrainPath, testFile, templates)
    }

    fun train() {

        lf.train(imgTrainPath, faceModelPath)
    }

    fun initTest() {
        if (testList.isEmpty()) {
            initTestList(testFile)
        }
        if (templatesList.isEmpty()) {
            initList(templates, templatesList)
        }
    }

    fun predict(s: String): Int {
        return lf.predict(s, faceModelPath)
    }

    fun getTemplate(predict: Int): String {
        if (templatesList.isEmpty()) {
            initList(templates, templatesList)
        }
        return templatesList[predict].toString()
    }

    fun isRight(img: String, pre: Int): Boolean {
        if (testList[img] == pre)
            return true
        return false
    }
}