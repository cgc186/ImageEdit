package opencv.bow

import org.opencv.core.MatOfDMatch
import org.opencv.core.MatOfKeyPoint
import org.opencv.features2d.DescriptorExtractor
import org.opencv.features2d.FeatureDetector
import org.opencv.imgproc.Imgproc
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.features2d.DescriptorMatcher


// 特征点匹配，值越大匹配度越高

fun imgMatching2() {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    val src_base = Imgcodecs.imread("D:\\test\\test5.jpg")
    val src_test = Imgcodecs.imread("D:\\test\\test3.jpg")
    val gray_base = Mat()
    val gray_test = Mat()
    // 转换为灰度
    Imgproc.cvtColor(src_base, gray_base, Imgproc.COLOR_RGB2GRAY)
    Imgproc.cvtColor(src_test, gray_test, Imgproc.COLOR_RGB2GRAY)
    // 初始化ORB检测描述子
    val featureDetector =
        FeatureDetector.create(FeatureDetector.ORB)//特别提示下这里opencv暂时不支持SIFT、SURF检测方法，这个好像是opencv(windows) java版的一个bug,本人在这里被坑了好久。
    val descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB)
    // 关键点及特征描述矩阵声明
    val keyPoint1 = MatOfKeyPoint()
    val keyPoint2 = MatOfKeyPoint()
    val descriptorMat1 = Mat()
    val descriptorMat2 = Mat()
    // 计算ORB特征关键点
    featureDetector.detect(gray_base, keyPoint1)
    featureDetector.detect(gray_test, keyPoint2)
    // 计算ORB特征描述矩阵
    descriptorExtractor.compute(gray_base, keyPoint1, descriptorMat1)
    descriptorExtractor.compute(gray_test, keyPoint2, descriptorMat2)
    var result = 0f
    // 特征点匹配
    println("test5：" + keyPoint1.size())
    println("test3：" + keyPoint2.size())
    if (!keyPoint1.size().empty() && !keyPoint2.size().empty()) {
        // FlannBasedMatcher matcher = new FlannBasedMatcher();
        val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_L1)
        val matches = MatOfDMatch()
        matcher.match(descriptorMat1, descriptorMat2, matches)
        // 最优匹配判断
        val minDist = 100.0
        val dMatchs = matches.toArray()
        var num = 0
        for (i in dMatchs.indices) {
            if (dMatchs[i].distance <= 2 * minDist) {
                result += dMatchs[i].distance * dMatchs[i].distance
                num++
            }
        }
        // 匹配度计算
        result /= num.toFloat()
    }
    println(result)
}