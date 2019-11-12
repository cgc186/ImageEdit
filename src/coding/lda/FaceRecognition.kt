package lda

import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

import javax.imageio.ImageIO
import java.awt.*
import java.io.*
import java.net.MalformedURLException
import java.util.ArrayList
import java.util.Arrays

import org.opencv.core.Core.*
import org.opencv.core.CvType.CV_32F
import org.opencv.core.CvType.CV_32FC1
import org.opencv.imgproc.Imgproc.INTER_LINEAR
import org.opencv.imgproc.Imgproc.equalizeHist
import kotlin.math.pow

class FaceRecognition internal constructor() {
    private var imgList: ArrayList<File>? = null
    private var list: ArrayList<String>? = null
    private var trainFaceMat: Mat? = null//样本训练矩阵
    private var meanFaceMat: Mat? = null//平均值矩阵
    private val MFMFileName = "meanFaceMat"
    private var normTrainFaceMat: Mat? = null//规格化训练样本矩阵
    private var matRows: Int = 0//矩阵行 height y
    private val matCols = 48 * 48//矩阵列 width x
    private var eigenVectors: Mat? = null//降维后特征向量
    private val vectorsFile = "eigenVectors"//特征向量保存文件地址
    private val eigenFile = "eigenFace"
    private var eigenFace: Mat? = null//样本的特征脸空间
    private var eigenTrainSample: Mat? = null//投影样本矩阵
    private val eigenTrainSampleFile = "eigenTrainSample"
    private var classMat: Mat? = null
    private var ldaTrainSample: Mat? = null


    init {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    }

    /***
     * 图像转化为灰度Mat矩阵
     * @param image 源图像
     * @return 灰度Mat矩阵
     */
    internal fun getGrayMatFromImg(image: Image?): Mat? {
        if (image != null) {
            val pixelReader = image.pixelReader
            val matImage = Mat(image.height.toInt(), image.width.toInt(), CV_32F)
            //遍历源图像每个像素，将其写入到目标图像
            var y = 0
            while (y < image.height) {
                var x = 0
                while (x < image.width) {
                    val color = pixelReader.getColor(x, y)
                    val gray = ((color.blue + color.green + color.red) / 3.0).toFloat()
                    matImage.put(y, x, gray.toDouble())
                    x++
                }
                y++
            }
            return matImage
        }
        return null
    }

    /***
     * Mat转化为Img
     * @param matImg Mat矩阵
     * @return Img图像
     */
    internal fun getImgFromMat(matImg: Mat?): Image? {
        if (matImg != null) {
            val wImage = WritableImage(matImg.width(), matImg.height())
            //得到像素写入器
            val pixelWriter = wImage.pixelWriter
            if (matImg.channels() == 1) {  //单通道图像
                val gray = FloatArray(matImg.height() * matImg.width())
                matImg.get(0, 0, gray)
                //遍历源图像每个像素，将其写入到目标图像
                for (y in 0 until matImg.height()) {
                    for (x in 0 until matImg.width()) {
                        val pixelIndex = y * matImg.width() + x
                        val color = Color.gray(gray[pixelIndex].toDouble())
                        pixelWriter.setColor(x, y, color)
                    }
                }
            } else if (matImg.channels() == 3) {    //3通道图像
                //遍历源图像每个像素，将其写入到目标图像
                for (y in 0 until matImg.height()) {
                    for (x in 0 until matImg.width()) {
                        val gray = IntArray(3)
                        matImg.get(y, x, gray)
                        val color = Color.rgb(gray[2], gray[1], gray[0])
                        pixelWriter.setColor(x, y, color)
                    }
                }
            }
            return wImage
        }
        return null
    }

    /***
     * 图像缩放
     * @param image 源图像
     * @param begin 起点坐标
     * @param end 终点坐标
     * @param zoom_multiples 缩放倍数
     * @return 图像Image
     */
    private fun getResizeImg(image: Image?, begin: Point, end: Point, zoom_multiples: Double): Image? {

        if (image != null) {
            val pixelReader = image.pixelReader
            //Mat matImage = new Mat((int) (image.getHeight()), (int) (image.getWidth()), CvType.CV_32F);

            val writableImage = WritableImage((end.getX() - begin.getX()).toInt(), (end.getY() - begin.getY()).toInt())
            val pixelWriter = writableImage.pixelWriter

            var y = begin.getY().toInt()
            while (y < end.getY()) {
                var x = begin.getX().toInt()
                while (x < end.getX()) {
                    val color = pixelReader.getColor(x, y)
                    val x1 = color.blue
                    val x2 = color.red
                    val x3 = color.green
                    val color2 = Color(x2, x3, x1, 1.0)
                    pixelWriter.setColor((x - begin.getX()).toInt(), (y - begin.getY()).toInt(), color2)
                    x++
                }
                y++
            }
            val src = getGrayMatFromImg(writableImage)
            val dst = Mat()
            //Imgproc.resize(src, dst, new Size(writableImage.getWidth() * zoom_multiples, writableImage.getWidth() * zoom_multiples), 0, 0, INTER_LINEAR);
            Imgproc.resize(src!!, dst, Size(48.0, 48.0), 0.0, 0.0, INTER_LINEAR)
            return getImgFromMat(dst)
        }
        return null
    }

    //直方图均衡化
    internal fun equalization(mat: Mat): Mat {
        mat.convertTo(mat, CvType.CV_8UC1, 255.0, 0.0)
        val dst = Mat()
        val mv = ArrayList<Mat>()
        Core.split(mat, mv)
        for (i in 0 until mat.channels()) {
            equalizeHist(mv[i], mv[i])
        }
        Core.merge(mv, dst)
        dst.convertTo(dst, CV_32FC1, 1.0 / 255, 0.0)
        return dst
    }

    //图像灰度归一化
    internal fun normalize(image: Image, begin: Point, end: Point, zoom_multiples: Double): Image? {
        val resizeImg = getResizeImg(image, begin, end, zoom_multiples)
        val mat = getGrayMatFromImg(resizeImg)
        //Mat eMat = equalization(mat);
        //return getImgFromMat(eMat);
        return getImgFromMat(mat)
        // return resizeImg;
    }

    //获取所有图像文件名
    internal fun getImages(dir: File, picNum: Int): ArrayList<File> {
        val files = dir.listFiles()
        val dirPath = dir.path
        val fileArrayList = ArrayList<File>()
        //System.out.println(files[0]);
        if (files != null) {
            println("图片共 " + files.size + " 张!")
            classMat = Mat(files.size, 1, CV_32FC1)
            for (i in 1..files.size / picNum) {
                for (j in 1..picNum) {
                    fileArrayList.add(File(dirPath + "/s" + i + "_" + j + ".bmp"))
                    classMat!!.put((i - 1) * picNum + j - 1, 0, i.toDouble())
                }
            }
            saveMatToFile(classMat!!, "classMat")
        }
        return fileArrayList
    }

    internal fun getTrainFace(arrayList: ArrayList<File>) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        matRows = arrayList.size
        imgList = arrayList
        saveImgListToFile()
        trainFaceMat = Mat(matRows, matCols, CV_32FC1)
        var countMat = 0
        for (file in arrayList) {
            try {
                val image = Image(file.toURI().toURL().toString())
                val mat = getGrayMatFromImg(image)
                //System.out.println(file);
                val eMat = equalization(mat!!)

                var z = 0
                val gray = DoubleArray((image.width * image.width).toInt())
                var x = 0
                while (x < image.width) {
                    var y = 0
                    while (y < image.height) {
                        gray[z++] = eMat.get(x, y)[0]
                        y++
                    }
                    x++
                }
                trainFaceMat!!.put(countMat++, 0, *gray)
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }

        }
        //outputMat(trainFaceMat);
    }

    //获取TrainFaceMat
    internal fun getTrainFaceMat(arrayList: ArrayList<File>) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        matRows = arrayList.size
        imgList = arrayList
        //System.out.println(imgList.toString());
        saveImgListToFile()
        trainFaceMat = Mat(matRows, matCols, CV_32FC1)
        var countMat = 0
        for (file in arrayList) {
            try {
                val image = Image(file.toURI().toURL().toString())
                //System.out.println(file);
                val pixelReader = image.pixelReader
                val pixelList = DoubleArray((image.width * image.height).toInt())
                var z = 0
                var x = 0
                while (x < image.width) {
                    var y = 0
                    while (y < image.height) {
                        val color = pixelReader.getColor(x, y)
                        val gray = ((color.blue + color.green + color.red) / 3.0).toFloat()
                        pixelList[z++] = gray.toDouble()
                        y++
                    }
                    x++
                }
                trainFaceMat!!.put(countMat++, 0, *pixelList)
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }

        }
        //outputMat(trainFaceMat);
    }

    //计算平均矩阵
    internal fun calMeanFaceMat() {
        //outputMat(trainFaceMat);
        meanFaceMat = Mat(1, matCols, CV_32FC1)
        var sum = 0.0
        for (c in 0 until matCols) {
            sum = 0.0
            for (r in 0 until matRows) {
                val tf = trainFaceMat!!.get(r, c)[0]
                sum += tf
            }
            val avg = sum / matRows
            //System.out.println("sum:" + sum + " avg:" + avg);
            meanFaceMat!!.put(0, c, avg)
        }
        //outputMat(meanFaceMat);
        saveMatToFile(meanFaceMat!!, MFMFileName)
    }

    //计算规格化样本矩阵
    internal fun calNormTrainFaceMat() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        normTrainFaceMat = Mat(matRows, matCols, CV_32FC1)//规格化样本矩阵
        //outputMat(meanFaceMat);
        for (x in 0 until matRows) {
            for (y in 0 until matCols) {
                normTrainFaceMat!!.put(x, y, trainFaceMat!!.get(x, y)[0] - meanFaceMat!!.get(0, y)[0])
            }
        }
    }

    //PCA识别比对
    internal fun calTestFaceMat(normalizeImg: Image): String {
        val mat = getGrayMatFromImg(normalizeImg)
        println("mat:" + mat!!.height() + "*" + mat.width())
        val testFaceMat = Mat(1, mat.height() * mat.width(), CV_32FC1)
        for (i in 0 until mat.height()) {
            for (j in 0 until mat.width()) {
                testFaceMat.put(0, i * mat.width() + j, *mat.get(i, j))
            }
        }
        println("testFaceMat:" + testFaceMat.height() + "*" + testFaceMat.width())
        readMeanFaceMat()
        println("meanFaceMat:" + meanFaceMat!!.height() + "*" + meanFaceMat!!.width())
        val normTestFaceMat = Mat()
        subtract(testFaceMat, meanFaceMat!!, normTestFaceMat)
        println("normTestFaceMat:" + normTestFaceMat.height() + "*" + normTestFaceMat.width())
        val eigenTestSample = Mat()
        readEigenFace()
        gemm(normTestFaceMat, eigenFace!!, 1.0, Mat(), 0.0, eigenTestSample)
        println("eigenTestSample:" + eigenTestSample.height() + "*" + eigenTestSample.width())
        //outputMat(eigenTestSample);
        val threshold = 0.7
        var min = 0.0
        var index = 0
        readEigenTrainSample()
        println("eigenTrainSample:" + eigenTrainSample!!.height() + "*" + eigenTrainSample!!.width())
        var distance = 0.0
        for (i in 0 until eigenTrainSample!!.height()) {
            distance = 0.0
            for (j in 0 until eigenTrainSample!!.width()) {
                distance += Math.pow(eigenTrainSample!!.get(i, j)[0] - eigenTestSample.get(0, j)[0], 2.0)
            }
            distance = Math.sqrt(distance)
            if (i == 0) {
                min = distance
                index = 0
            } else {
                if (min > distance) {
                    min = distance
                    index = i
                }
            }
        }
        println("index:$index min:$min")
        readImgList()
        println(list!!.size)
        println(list!![index])
        return list!![index]
    }

    //PCA训练
    internal fun calculateEigenTrain() {
        //计算特征值和特征向量
        //特征值
        val eigenvalues = Mat()
        //特征向量
        val eigenvectors = Mat()
        val dst = Mat()//转置矩阵
        mulTransposed(normTrainFaceMat!!, dst, false)//计算矩阵与转置矩阵点乘
        eigen(dst, eigenvalues, eigenvectors)//
        //outputMat(eigenvectors);
        //outputMat(eigenvalues);
        //特征向量行数
        val eigenRow = eigenvectors.height()
        //特征向量列数
        val eigenCol = eigenvectors.width()
        //eigenvalues.height()*eigenvalues.width()  400*1
        //特征值求和
        var ValuesSum = 0.0
        for (i in 0 until eigenvalues.height()) {
            ValuesSum += eigenvalues.get(i, 0)[0]
        }
        //System.out.println("ValuesSum:" + ValuesSum);
        // 前m个特征值大于90%
        var sum = 0.0
        var m = 0
        for (i in 0 until eigenvalues.height()) {
            sum += eigenvalues.get(i, 0)[0]
            if (sum >= ValuesSum * 0.9) {
                m = i
                break
            }
        }
        //System.out.println("Sum:" + sum);
        println("m:$m")
        //特征向量降维
        eigenVectors = Mat(eigenRow, m, CV_32FC1)
        for (i in 0 until eigenRow) {
            for (j in 0 until m) {
                eigenVectors!!.put(i, j, eigenvectors.get(i, j)[0] / Math.sqrt(eigenvalues.get(j, 0)[0]))
            }
        }
        //outputMat(eigenVectors);
        saveMatToFile(eigenVectors!!, vectorsFile)
        //获得训练样本的特征脸空间
        val TnormTrainFaceMat = Mat()
        transpose(normTrainFaceMat!!, TnormTrainFaceMat)
        eigenFace = Mat()
        gemm(TnormTrainFaceMat, eigenVectors!!, 1.0, Mat(), 0.0, eigenFace!!)//乘
        //行数
        val eigenFaceRow = eigenFace!!.height()
        //列数
        val eigenFaceCol = eigenFace!!.width()
        saveMatToFile(eigenFace!!, eigenFile)
        //训练样本在特征脸空间的投影
        eigenTrainSample = Mat()
        println("normTrainFaceMat:" + normTrainFaceMat!!.height() + "*" + normTrainFaceMat!!.width() + "\neigenFaceMat:" + eigenFace!!.height() + "*" + eigenFace!!.width())
        gemm(normTrainFaceMat!!, eigenFace!!, 1.0, Mat(), 0.0, eigenTrainSample!!)//M*N * N*m -> M*m
        println("eigenTrainSample:" + eigenTrainSample!!.height() + "*" + eigenTrainSample!!.width())
        //outputMat(eigenTrainSample);
        saveMatToTXTFile(eigenTrainSample!!, "eigenTrainSampleTXT.txt")
        saveMatToFile(eigenTrainSample!!, eigenTrainSampleFile)
    }

    //LDA识别
    internal fun calLDATestSample(normalizeImg: Image): String {
        val mat = getGrayMatFromImg(normalizeImg)
        println("mat:" + mat!!.height() + "*" + mat.width())
        val testFaceMat = Mat(1, mat.height() * mat.width(), CV_32FC1)
        for (i in 0 until mat.height()) {
            for (j in 0 until mat.width()) {
                testFaceMat.put(0, i * mat.width() + j, *mat.get(i, j))
            }
        }
        println("testFaceMat:" + testFaceMat.height() + "*" + testFaceMat.width())
        readMeanFaceMat()
        println("meanFaceMat:" + meanFaceMat!!.height() + "*" + meanFaceMat!!.width())
        val normTestFaceMat = Mat()
        subtract(testFaceMat, meanFaceMat!!, normTestFaceMat)
        println("normTestFaceMat:" + normTestFaceMat.height() + "*" + normTestFaceMat.width())
        val eigenTestSample = Mat()
        readEigenFace()
        gemm(normTestFaceMat, eigenFace!!, 1.0, Mat(), 0.0, eigenTestSample)
        println("eigenTestSample:" + eigenTestSample.height() + "*" + eigenTestSample.width())
        //outputMat(eigenTestSample);
        //测试人脸在LDA投影空间上进行投影
        val ldaTestSample = Mat()
        val ldaeigenVectors = readMatFromFile("ldaeigenVectors")
        println("ldaeigenVectors:" + ldaeigenVectors!!.height() + "*" + ldaeigenVectors.width())
        //outputMat(ldaeigenVectors);
        gemm(eigenTestSample, ldaeigenVectors, 1.0, Mat(), 0.0, ldaTestSample)
        println("ldaTestSample:" + ldaTestSample.height() + "*" + ldaTestSample.width())
        //计算欧式距离
        val threshold = 0.7
        readLDATrainSample()
        //outputMat(ldaTestSample);
        println("ldaTrainSample:" + ldaTrainSample!!.height() + "*" + ldaTrainSample!!.width())
        var min = 0.0
        var index = 0
        var distance = 0.0
        for (i in 0 until ldaTrainSample!!.height()) {
            distance = 0.0
            for (j in 0 until ldaTrainSample!!.width()) {
                distance += (ldaTrainSample!!.get(i, j)[0] - ldaTestSample.get(0, j)[0]).pow(2.0)
            }
            distance = Math.sqrt(distance)
            if (i == 0) {
                min = distance
                index = 0
            } else {
                if (min > distance) {
                    min = distance
                    index = i
                }
            }
        }
        println("index:$index min:$min")
        readImgList()
        println(list!!.size)
        println(list!![index])
        return list!![index]
    }

    //LDA训练
    internal fun LDA() {
        //计算eigenTrainSample 均值
        var t = 0
        do {

            val etsHeight = eigenTrainSample!!.height()
            val etsWidth = eigenTrainSample!!.width()
            val u = Mat(1, etsWidth, CV_32FC1)//1*m
            var avg = 0.0
            for (j in 0 until etsWidth) {
                avg = 0.0
                for (i in 0 until etsHeight) {
                    avg += eigenTrainSample!!.get(i, j)[0]
                }
                avg = avg / etsHeight
                u.put(0, j, avg)
            }
            //System.out.println("U:");
            //outputMat(u);
            //计算类均值人脸
            readClassMat()
            //outputMat(classMat);
            val c = classMat!!.get(classMat!!.height() - 1, 0)[0].toInt()
            println("c:$c")
            val ui = Mat(c, etsWidth, CV_32FC1)
            var now = classMat!!.get(0, 0)[0].toInt()
            val sum = DoubleArray(etsWidth)
            var num = 1
            for (i in 0 until etsHeight) {
                val cn = classMat!!.get(i, 0)[0].toInt()
                if (cn == now) {
                    num++
                    for (j in 0 until etsWidth) {
                        sum[j] += eigenTrainSample!!.get(i, j)[0]
                    }
                } else {
                    for (j in 0 until etsWidth) {
                        sum[j] /= num.toDouble()
                    }
                    num = 1
                    ui.put(now - 1, 0, *sum)
                    for (j in 0 until etsWidth) {
                        sum[j] = 0.0
                    }
                    now = cn
                    for (j in 0 until etsWidth) {
                        sum[j] += eigenTrainSample!!.get(i, j)[0]
                    }
                }
                if (i == etsHeight - 1) {
                    for (j in 0 until etsWidth) {
                        sum[j] /= num.toDouble()
                    }
                    ui.put(now - 1, 0, *sum)
                }
            }
            println("num:$num")
            //System.out.println("ui:");
            //outputMat(ui);
            //计算类间离散度矩阵
            val Sb = Mat(etsWidth, etsWidth, CV_32FC1)
            for (i in 0 until c) {
                val dst = Mat()
                subtract(u, ui.row(i), dst)
                // outputMat(dst);
                val m2 = Mat()
                transpose(dst, dst)
                mulTransposed(dst, m2, false)
                add(Sb, m2, Sb)
            }
            //outputMat(Sb);//√
            //System.out.println("\n\n");
            //计算类内离散度矩阵
            val Sw = Mat(etsWidth, etsWidth, CV_32FC1)
            for (i in 0 until c) {
                val w = Mat(etsWidth, etsWidth, CV_32FC1)
                for (j in 0 until num) {
                    val dst = Mat()
                    subtract(eigenTrainSample!!.row(i * num + j), ui.row(i), dst)
                    //outputMat(dst);
                    transpose(dst, dst)
                    val m2 = Mat()
                    mulTransposed(dst, m2, false)
                    add(w, m2, w)
                }
                add(Sw, w, Sw)
            }
            //outputMat(Sw);//√
            //计算Sw-1Sb 矩阵的特征值和特征向量
            val Sw1 = Mat(Sw.width(), Sw.height(), Sw.type())//Sw.width(), Sw.height(), Sw.type()
            invert(Sw, Sw1, DECOMP_EIG)
            // FIXME: 2018/6/8 逆矩阵计算有时有问题
            //outputMat(Sw1);//√
            println("Sw1:" + Sw1.height() + "*" + Sw1.width())
            val ldaEigenValues = Mat()
            val ldaEigenVectors = Mat()
            val dst = Mat()
            gemm(Sw1, Sb, 1.0, Mat(), 0.0, dst)
            //outputMat(dst);//√
            eigen(dst, ldaEigenValues, ldaEigenVectors)
            println("ldaEigenVectors:" + ldaEigenVectors.height() + "*" + ldaEigenVectors.width())
            println("ldaEigenValues:" + ldaEigenValues.height() + "*" + ldaEigenValues.width())
            //outputMat(ldaEigenVectors);
            //outputMat(ldaEigenValues);//√
            //对特征空间降维

            for (i in 0 until ldaEigenValues.height()) {
                if (ldaEigenValues.get(i, 0)[0] > 0) {
                    t = i
                } else {
                    break
                }
            }
            val ldaeigenVectors = Mat(ldaEigenVectors.height(), t, CV_32FC1)
            for (i in 0 until ldaEigenVectors.height()) {
                for (j in 0 until t) {
                    ldaeigenVectors.put(i, j, ldaEigenVectors.get(i, j)[0] / Math.sqrt(ldaEigenValues.get(j, 0)[0]))
                }
            }
            println("t:$t")

            saveMatToFile(ldaeigenVectors, "ldaeigenVectors")
            //outputMat(ldaeigenVectors);
            ldaTrainSample = Mat()
            //readEigenTrainSample();
            gemm(eigenTrainSample!!, ldaeigenVectors, 1.0, Mat(), 0.0, ldaTrainSample!!)
            //outputMat(ldaTrainSample);
            saveMatToFile(ldaTrainSample!!, "ldaTrainSample")

        } while (t < 20)
    }

    private fun saveMatToFile(mat: Mat, filename: String) {
        try {
            val outputStream = DataOutputStream(FileOutputStream(filename))
            outputStream.writeInt(mat.height())
            outputStream.writeInt(mat.width())
            for (i in 0 until mat.height()) {
                for (j in 0 until mat.width()) {
                    outputStream.writeDouble(mat.get(i, j)[0])
                }
            }
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun saveMatToTXTFile(mat: Mat, fileName: String) {
        try {
            val fileWriter = FileWriter(fileName)
            val bw = BufferedWriter(fileWriter)
            for (i in 0 until mat.height()) {
                for (j in 0 until mat.width()) {
                    bw.write(mat.get(i, j)[0].toString() + " ")
                }
                bw.write("\n")
            }
            bw.close()
            fileWriter.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun saveImgListToFile() {
        try {
            val fileWriter = FileWriter("imgList")
            val bw = BufferedWriter(fileWriter)
            for (file in imgList!!) {
                bw.write(file.path + "\n")
            }
            bw.close()
            fileWriter.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun readMatFromFile(fileName: String): Mat? {
        try {
            val inputStream = DataInputStream(FileInputStream(fileName))
            val row = inputStream.readInt()
            val col = inputStream.readInt()
            val mat = Mat(row, col, CV_32FC1)
            for (i in 0 until row) {
                for (j in 0 until col) {
                    mat.put(i, j, inputStream.readDouble())
                }
            }
            return mat
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    private fun readImgList() {
        try {
            val `is` = FileInputStream(File("imgList"))
            var line: String?
            val br = BufferedReader(InputStreamReader(`is`))
            line = br.readLine()
            list = ArrayList()
            while (line != null) {
                list!!.add(line)
                line = br.readLine()
            }
            br.close()
            `is`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun readClassMat() {
        try {
            val inputStream = DataInputStream(FileInputStream("classMat"))
            val row = inputStream.readInt()
            val col = inputStream.readInt()
            classMat = Mat(row, col, CV_32FC1)
            for (i in 0 until row) {
                for (j in 0 until col) {
                    classMat!!.put(i, j, inputStream.readDouble())
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun readLDATrainSample() {
        try {
            val inputStream = DataInputStream(FileInputStream("ldaTrainSample"))
            val row = inputStream.readInt()
            val col = inputStream.readInt()
            ldaTrainSample = Mat(row, col, CV_32FC1)
            for (i in 0 until row) {
                for (j in 0 until col) {
                    ldaTrainSample!!.put(i, j, inputStream.readDouble())
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun readEigenTrainSample() {
        try {
            val inputStream = DataInputStream(FileInputStream(eigenTrainSampleFile))
            val row = inputStream.readInt()
            val col = inputStream.readInt()
            eigenTrainSample = Mat(row, col, CV_32FC1)
            for (i in 0 until row) {
                for (j in 0 until col) {
                    eigenTrainSample!!.put(i, j, inputStream.readDouble())
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun readMeanFaceMat() {
        try {
            val inputStream = DataInputStream(FileInputStream(MFMFileName))
            val row = inputStream.readInt()
            val col = inputStream.readInt()
            meanFaceMat = Mat(row, col, CV_32FC1)
            for (i in 0 until row) {
                for (j in 0 until col) {
                    meanFaceMat!!.put(i, j, inputStream.readDouble())
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    internal fun readEigenFace() {
        try {
            val inputStream = DataInputStream(FileInputStream(eigenFile))
            val row = inputStream.readInt()
            val col = inputStream.readInt()
            eigenFace = Mat(row, col, CV_32FC1)
            for (i in 0 until row) {
                for (j in 0 until col) {
                    eigenFace!!.put(i, j, inputStream.readDouble())
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    //从文件读取特征向量
    internal fun readEigenVectors() {
        try {
            val inputStream = DataInputStream(FileInputStream(vectorsFile))
            val row = inputStream.readInt()
            val col = inputStream.readInt()
            eigenVectors = Mat(row, col, CV_32FC1)
            for (i in 0 until row) {
                for (j in 0 until col) {
                    val d = inputStream.readDouble()
                    eigenVectors!!.put(i, j, inputStream.readDouble())
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    //输出Mat矩阵
    fun outputMat(mat: Mat) {
        for (i in 0 until mat.height()) {
            for (j in 0 until mat.width()) {
                print(Arrays.toString(mat.get(i, j)) + " ")
            }
            println()
        }
    }

    companion object {

        //判断是否为图片
        private fun isImage(file: File): Boolean {
            var flag = false
            try {
                val imageInputStream = ImageIO.createImageInputStream(file) ?: return false
                imageInputStream.close()
                flag = true
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return flag
        }

        internal fun readPicNumFromFile(): Int {
            try {
                val fileReader = FileReader("picNum.txt")
                val br = BufferedReader(fileReader)
                val picNum = br.read()
                br.close()
                fileReader.close()
                return picNum
            } catch (e: IOException) {
                return 9
            }

        }

        internal fun savePicNumToFile(picnum: Int) {
            try {
                val fileOutputStream = FileOutputStream("picNum.txt")
                fileOutputStream.write(picnum)
                fileOutputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}
