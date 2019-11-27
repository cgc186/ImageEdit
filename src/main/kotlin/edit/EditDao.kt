package edit

import cluster.ImageCluster

object EditDao {
    private val editImage = editjni.EditImage()
    private val ic = ImageCluster()

    fun surf(path: String, savePath: String) {
//        val path = "E:/备份/OneDrive - Dezhkeda/壁纸/新建文件夹 (7)/1.jpg"
//        val savePath = "D:/11out.png"
        editImage.surf(path, savePath)
    }

    fun cornerHairrs(path: String, savePath: String) {
        editImage.cornerHairrs(path, savePath)
    }

    fun imageCluster(path: String, savePath: String, type: Int) {
        ic.kmeans(path, savePath, 3, 10, type)
    }
}