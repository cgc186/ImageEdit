package lda

class RecognitionThread internal constructor() : Thread() {
    private val threadName: String? = null
    private var thread: Thread? = null

    override fun run() {
        val FR = FaceRecognition()
        FR.readEigenVectors()
        FR.readEigenFace()
        FR.calNormTrainFaceMat()
    }


    @Synchronized
    override fun start() {
        if (thread == null) {
            thread = Thread(this, threadName!!)
            thread!!.start()
        }
    }
}
