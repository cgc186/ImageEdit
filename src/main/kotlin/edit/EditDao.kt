package edit

import caffejni.Caffe
import cluster.ImageCluster
//import org.bytedeco.javacpp.opencv_core.*
//import org.bytedeco.javacpp.opencv_imgcodecs.imread
//import org.bytedeco.javacpp.opencv_imgcodecs.imwrite
//import org.bytedeco.javacpp.opencv_imgproc.COLOR_RGB2GRAY
//import org.bytedeco.javacpp.opencv_imgproc.cvtColor

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

//    fun pca(imgPath: String, savePath: String) {
//        val img: Mat = imread(imgPath)
//        cvtColor(img, img, COLOR_RGB2GRAY)
//        val pca = PCA(img, Mat(), CV_PCA_DATA_AS_COL, 120)
//        val dst: Mat = pca.project(img)
//        val src: Mat = pca.backProject(dst)
//        imwrite(savePath, src)
//    }

    fun getPca(imgPath: String, savePath: String) {

        val c = Caffe()
        c.pca(imgPath, savePath);
    }
}