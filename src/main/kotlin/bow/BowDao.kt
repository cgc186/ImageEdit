package bow

import bowjni.Bow
import java.io.File

object BowDao {
    private val bow = Bow()

    fun train(
        trainFolder: String,
        templateFolder: String,
        testFolder: String,
        resultFolder: String
    ) {
        val clusters = getNumber(trainFolder)
        val dataFolder = getDataFolder(trainFolder)
        bow.train(clusters, dataFolder, trainFolder, templateFolder, testFolder, resultFolder)
    }

    fun categoryImage(
        trainPicPath: String,
        dataFolder: String
    ): String {
        return bow.categoryImage(trainPicPath, dataFolder)
    }

    fun getTemplateImage(type: String, templateFolder: String): String {
        return "$templateFolder$type.jpg"
    }

    fun categoryBySvm(
        dataFolder: String,
        testFolder: String,
        resultFolder: String,
        templateFolder: String,
        flag: Int
    ){
        bow.categoryBySvm(dataFolder, testFolder, resultFolder, templateFolder, flag)
    }

    private fun getNumber(filename: String): Int {
        var number = 0

        val files = File(filename)
        val list = files.list()
        list.forEach {
            val img = File("$filename/$it")
            val listFiles = img.listFiles()
            var size = listFiles.size
            for (i in 0 until size) {
                number++
            }
        }
        return number
    }

    fun getTestList(filename: String): MutableList<String> {
        var testlist = mutableListOf<String>()
        val files = File(filename)
        val list = files.listFiles()
        list.forEach {
            testlist.add(it.absolutePath)
        }
        return testlist
    }

    fun getDataFolder(trainFolder: String): String {
        val strList = trainFolder.split("\\")
//        strList.forEach {
//            println(it)
//        }
        val l = strList[strList.size - 1]
        return trainFolder.substring(0, (trainFolder.length - l.length))
    }
}