package lda

import javafx.embed.swing.SwingFXUtils
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.videoio.VideoCapture

import javax.imageio.ImageIO
import java.awt.*
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.awt.image.WritableRaster
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.ArrayList
import java.util.Objects

class UIController {
    @FXML
    private val AnchorPane: Pane? = null
    @FXML
    private val bt1: Button? = null
    @FXML
    private val bt2: Button? = null
    @FXML
    private val img1: ImageView? = null
    @FXML
    private val img2: ImageView? = null
    @FXML
    private val pane_img1: Pane? = null
    @FXML
    private val pane3_1: Pane? = null
    @FXML
    private val imgView3_1: ImageView? = null
    @FXML
    private val imgView3_2: ImageView? = null
    @FXML
    private val imgView3_3: ImageView? = null
    @FXML
    private val label3_1: Label? = null
    @FXML
    private val label3_2: Label? = null
    @FXML
    private val label3_3: Label? = null
    @FXML
    private val label3_4: Label? = null
    @FXML
    private val label4_1: Label? = null
    @FXML
    private val imgView4_1: ImageView? = null
    @FXML
    private val pb2_1: ProgressBar? = null
    @FXML
    private val label2_1: Label? = null

    @FXML
    private val text2_1_1: TextField? = null
    @FXML
    private val label2_1_1: Label? = null

    private var image: Image? = null
    private var normalizeImg: Image? = null
    private var click = false
    private var p1_x: Double = 0.toDouble()
    private var p1_y: Double = 0.toDouble()
    private var clickCount = 0
    private val circles = ArrayList<Circle>()
    private var imgList = ArrayList<File>()
    private var imgView = ""
    private var picNum = 9//每组样本数量
    private var imgPath = ""
    private var takePhotoFlag = false


    fun setImg1() {
        selectPic()
        img1!!.image = image
    }

    fun setImgView3_1() {
        label3_4!!.text = " "
        selectPic()
        imgView3_1!!.image = image
        val path = imgPath.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        label3_1!!.text = path[path.size - 1]
    }

    //选择图片
    fun selectPic() {
        takePhotoFlag = false
        clickCount = 0
        val filechooser = FileChooser()
        //String picDir = "C:\\Users\\tzz\\Desktop\\图像处理课程设计2018秋\\人脸测试库";
        //String picDir = "C:\\Users\\tzz\\Desktop\\图像处理课程设计2018秋\\新建文件夹 (2)";
        val picDir = "E:\\备份\\OneDrive - Dezhkeda\\壁纸"
        filechooser.initialDirectory = File(picDir)
        val stage = Stage()
        val file = filechooser.showOpenDialog(stage)
        if (file != null) {
            println(file)
            try {
                imgPath = file.toURI().toURL().toString()
                println(imgPath)
                image = Image(imgPath)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            imgPath = ""
        }
    }

    /***
     * 鼠标选点
     * @param event ActionEvent
     */
    fun onMouseClick(event: MouseEvent) {
        if ((img1 != null || imgView3_1 != null) && click) {
            clickCount++
            val circle = Circle(event.x, event.y, 3.0)
            circles.add(circle)
            circle.fill = Color.RED
            if (imgView == "img2") {
                pane_img1!!.children.add(circle)
            } else if (imgView == "imgView3_2") {
                pane3_1!!.children.add(circle)
            }
            if (clickCount >= 2) {
                val p2_x = event.x
                val p2_y = event.y

                val distance = Math.sqrt(Math.pow(p1_x - p2_x, 2.0) + Math.pow(p1_y - p2_y, 2.0))
                val x: Double
                val y: Double
                x = (p1_x + p2_x) / 2
                y = (p1_y + p2_y) / 2

                val imgHeight = image!!.height
                val imgWidth = image!!.width
                //double pw = pane_img1.getWidth(), ph = pane_img1.getHeight();//pane height width
                var pw = 0.0
                var ph = 0.0
                if (imgView == "img2") {
                    pw = pane_img1!!.width
                    ph = pane_img1.height//pane height width
                } else if (imgView == "imgView3_2") {
                    pw = pane3_1!!.width
                    ph = pane3_1.height//pane height width
                }
                if (imgHeight > imgWidth) {
                    pw = imgWidth * ph / imgHeight
                } else {
                    ph = pw * imgHeight / imgWidth
                }

                val zoom_multiples = 48 / (Math.sqrt(
                    Math.pow(
                        (p1_x - p2_x) * imgWidth / pw,
                        2.0
                    ) + Math.pow((p1_y - p2_y) * imgHeight / ph, 2.0)
                ) * 2)

                val begin = Point()
                val end = Point()
                begin.x = (x - distance).toInt()
                begin.x = (begin.getX() * imgWidth / pw).toInt()
                begin.y = (y - distance * 0.5).toInt()
                begin.y = (begin.getY() * imgHeight / ph).toInt()
                end.x = (x + distance).toInt()
                end.x = (end.getX() * imgWidth / pw).toInt()
                end.y = (y + distance * 1.5).toInt()
                end.y = (end.getY() * imgHeight / ph).toInt()
                if (begin.x < 0) {
                    begin.x = 0
                }
                if (begin.y < 0) {
                    begin.y = 0
                }
                if (end.x > imgWidth) {
                    end.x = imgWidth.toInt()
                }
                if (end.y > imgHeight) {
                    end.y = imgHeight.toInt()
                }
                //从界面上删除选中点
                for (circle1 in circles) {
                    if (imgView == "img2") {
                        pane_img1!!.children.remove(circle1)
                    } else if (imgView == "imgView3_2") {
                        pane3_1!!.children.remove(circle1)
                    }
                }
                val fr = FaceRecognition()
                normalizeImg = fr.normalize(image!!, begin, end, zoom_multiples)

                //                Mat src = fr.getGrayMatFromImg(image);
                //                Mat dst = new Mat();
                //                Imgproc.resize(src, dst, new Size(48, 48), 0, 0, INTER_LINEAR);
                //                normalizeImg = fr.getImgFromMat(dst);
                //                normalizeImg = fr.getImgFromMat(fr.equalization(fr.getGrayMatFromImg(normalizeImg)));
                //均衡化显示
                val image = fr.getImgFromMat(fr.getGrayMatFromImg(normalizeImg)?.let { fr.equalization(it) })
                if (imgView == "img2") {
                    img2!!.image = image//normalizeImg
                } else if (imgView == "imgView3_2") {
                    imgView3_2!!.image = image//normalizeImg
                }
            } else {
                p1_x = event.x
                p1_y = event.y
            }
        }
    }

    @FXML
    fun setImg2() {
        click = true
        imgView = "img2"
    }

    @FXML
    fun setImgView3_2() {
        click = true
        imgView = "imgView3_2"
    }


    /***
     * 保存图片
     * @param event ActionEvent
     */
    fun savePic338(event: ActionEvent) {
        val fileChooser = FileChooser()
        fileChooser.title = "Save Image"
        val picDir = "E:\\备份\\OneDrive - Dezhkeda\\壁纸"
        fileChooser.initialDirectory = File(picDir)
        fileChooser.extensionFilters.addAll(
            FileChooser.ExtensionFilter("JPG", "*.jpg"),
            FileChooser.ExtensionFilter("PNG", "*.png"),
            FileChooser.ExtensionFilter("All Images", "*.*")
        )
        val FR = FaceRecognition()
        val stage = Stage()
        val file = fileChooser.showSaveDialog(stage)
        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(img2!!.image, null), "png", file)
            } catch (ex: IOException) {
                println(ex.message)
            }

        }

    }


    /***
     * 选择训练文件夹
     * @param actionEvent ActionEvent
     */
    fun selectDir338(actionEvent: ActionEvent) {
        picNum = FaceRecognition.readPicNumFromFile()
        println("picNum:$picNum")
        val directoryChooser = DirectoryChooser()
        val path = "E:\\备份\\OneDrive - Dezhkeda\\壁纸"
        directoryChooser.initialDirectory = File(path)
        val stage = Stage()
        val dir = directoryChooser.showDialog(stage)
        if (dir != null) {
            val FR = FaceRecognition()
            imgList = FR.getImages(dir, picNum)
            pb2_1!!.progress = 1.0
        }
    }


    fun openSecondStage338() {
        try {
            val secondStage = Stage()
            val root1 = FXMLLoader.load<javafx.scene.layout.AnchorPane>(
                Objects.requireNonNull<URL>(
                    javaClass.classLoader.getResource("/UI2.fxml")
                )
            )
            val secondScene = Scene(root1)
            secondStage.title = "设置"
            secondStage.scene = secondScene
            secondStage.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun setPicNum() {
        try {
            if (text2_1_1!!.text != null) {
                picNum = Integer.parseInt(text2_1_1.text)
                println("picNum:$picNum")
                FaceRecognition.savePicNumToFile(picNum)
                //System.out.println(FaceRecognition.readPicNumFromFile());
                label2_1_1!!.text = "修改成功"
            }
        } catch (e: Exception) {
            label2_1_1!!.text = "输入错误"
        }

    }

    //样本训练
    @Throws(InterruptedException::class)
    fun training338() {
        val thread = TrainThread("training", imgList)
        thread.start()
    }

    //训练未处理图像集
    @Throws(InterruptedException::class)
    fun training2338() {
        val thread = TrainThread("training2", imgList)
        thread.start()
    }

    //LDA人脸识别
    fun LDA338() {
        if (normalizeImg != null) {
            val FR = FaceRecognition()
            val mat = FR.getGrayMatFromImg(normalizeImg)
            val eMat = mat?.let { FR.equalization(it) }
            val image = FR.getImgFromMat(eMat)
            //String result = FR.calTestFaceMat(normalizeImg);
            val result = image?.let { FR.calLDATestSample(it) }//normalize
            val path = result?.split("\\\\".toRegex())?.dropLastWhile { it.isEmpty() }!!.toTypedArray()
            label3_2!!.text = path[path.size - 1]
            println(result)
            val name = path[path.size - 1].split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            imgView3_3!!.image = Image("file:/$result")
            val imgpath = imgPath.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            println(name + " " + imgpath[imgpath.size - 1])
            if (!takePhotoFlag) {
                if (imgpath[imgpath.size - 1].split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0] == name) {
                    label3_3!!.text = "LDA识别成功，结果为：$name"
                } else {
                    label3_4!!.text = "LDA识别错误，启动PCA识别"
                    label3_3!!.text = ""
                    PCA338()
                }
            } else {
                label3_3!!.text = "  "
            }
        }
    }

    //PCA图像识别
    fun PCA338() {
        if (normalizeImg != null) {
            val FR = FaceRecognition()
            val mat = FR.getGrayMatFromImg(normalizeImg)
            val eMat = mat?.let { FR.equalization(it) }
            val image = FR.getImgFromMat(eMat)
            val result = image?.let { FR.calTestFaceMat(it) }
            //String result = FR.calLDATestSample(image);//normalize
            val path = result!!.split("\\\\".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            label3_2!!.text = path[path.size - 1]
            println(result)
            val name = path[path.size - 1].split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            imgView3_3!!.image = Image("file:/$result")
            val imgpath = imgPath.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            println(name + " " + imgpath[imgpath.size - 1])
            if (!takePhotoFlag) {
                if (imgpath[imgpath.size - 1].split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0] == name) {
                    label3_3!!.text = "PCA识别成功，结果为：$name"
                } else {
                    label3_3!!.text = "识别错误"
                }
            } else {
                label3_3!!.text = "  "
            }
        }
    }

    //摄像头采集样本
    fun takePhoto() {
        takePhotoFlag = true
        clickCount = 0
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        CameraBasic338.photo()
        val capture = VideoCapture(0)
        val matrix = Mat()
        capture.read(matrix)
        if (capture.isOpened) {
            if (capture.read(matrix)) {
                val bufferedImage = BufferedImage(matrix.width(), matrix.height(), BufferedImage.TYPE_3BYTE_BGR)
                val raster = bufferedImage.raster
                val dataBuffer = raster.dataBuffer as DataBufferByte
                val data = dataBuffer.data
                matrix.get(0, 0, data)
                val WritableImage = SwingFXUtils.toFXImage(bufferedImage, null)
                Imgcodecs.imwrite("photo.bmp", matrix)
                image = WritableImage
                img1!!.image = image
            }
        }
        capture.release()
    }

    //摄像头识别
    fun takePhoto338() {
        label3_1!!.text = ""
        //imgPath = null;
        takePhotoFlag = true
        clickCount = 0
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        CameraBasic338.photo()
        val capture = VideoCapture(0)
        val matrix = Mat()
        capture.read(matrix)
        if (capture.isOpened) {
            if (capture.read(matrix)) {
                val bufferedImage = BufferedImage(matrix.width(), matrix.height(), BufferedImage.TYPE_3BYTE_BGR)
                val raster = bufferedImage.raster
                val dataBuffer = raster.dataBuffer as DataBufferByte
                val data = dataBuffer.data
                matrix.get(0, 0, data)
                val WritableImage = SwingFXUtils.toFXImage(bufferedImage, null)
                Imgcodecs.imwrite("photo2.bmp", matrix)
                //图片处理-》 未处理-》 直方均衡化
                //图片 -》 直方均衡化
                //FaceRecognition FR = new FaceRecognition();
                //Mat mat = FR.getGrayMatFromImg(WritableImage);
                //Mat eMat = FR.equalization(mat);
                //Image img = FR.getImgFromMat(eMat);
                //normalizeImg = img;
                //imgView3_1.setImage(img);//sanpshot.jpg
                //image = img;
                image = WritableImage
                imgView3_1!!.image = image
            }
        }
        capture.release()
    }
}