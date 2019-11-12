package lda

import java.io.File
import java.util.ArrayList

class TrainThread internal constructor(private val threadName: String, private val imgList: ArrayList<File>) :
    Thread() {
    private var thread: Thread? = null

    override fun run() {
        println("\n图像训练开始!")
        val FR = FaceRecognition()
        if (threadName == "training") {
            FR.getTrainFaceMat(imgList)//处理完成图片
        } else {
            FR.getTrainFace(imgList)//未处理图片
        }
        FR.calMeanFaceMat()
        FR.calNormTrainFaceMat()
        FR.calculateEigenTrain()
        FR.LDA()
        println("样本训练完成！\n")
    }

    override fun start() {
        if (thread == null) {
            thread = Thread(this, threadName)
            thread!!.start()
        }
    }
}