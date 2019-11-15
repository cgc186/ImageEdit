package lda

import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar


//-Djava.library.path=E:\opencv\opencv3.4.1\build\java\x64;E:\opencv\opencv3.4.1\build\x64\vc15

fun main() {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    val mat = Mat.eye(3, 3, CvType.CV_8UC1)
    println("mat = " + mat.dump())
}