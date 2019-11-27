package edit

import editjni.EditImage

object EditDao {
    private val editImage = editjni.EditImage()

    fun surf(path: String, savePath: String) {
//        val path = "E:/备份/OneDrive - Dezhkeda/壁纸/新建文件夹 (7)/1.jpg"
//        val savePath = "D:/11out.png"
        editImage.surf(path, savePath)
    }

    fun cornerHairrs(path: String, savePath: String) {
        editImage.cornerHairrs(path, savePath)
    }
}