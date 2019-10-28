package opencv.bow


import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar


//-Djava.library.path=E:\opencv\opencv3.4.1\build\java\x64;E:\opencv\opencv3.4.1\build\x64\vc15

//object Test {
//
//    @JvmStatic
//    fun main(args: Array<String>) {
//        // TODO Auto-generated method stub
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
//        val mat = Mat.eye(3, 3, CvType.CV_8UC1)
//        println("mat = " + mat.dump())
//    }
//
//}

fun main() {
    val clusters = 1000
    val b = Features(clusters)

    //特征聚类
    b.bulidVacab()
    //构造BOW
    b.computeBowImage()
    //训练分类器
    b.trainSvm()
    //将测试图片分类
    b.categoryBySvm()
}