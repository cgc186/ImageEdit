package bow.main

import bow.main.Train
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


fun main() {
    //图片路径
    val imgBase = "E:\\编程\\kotlin\\images\\training"
    //类别
    val categories = arrayOf("Phoning")

    //("Phoning", "PlayingGuitar", "RidingBike", "RidingHorse", "Running", "Shooting")

    //实物样本数量
    val cateSample = 10
    //输出路径
    val outputArff = "E:\\编程\\kotlin\\images\\train.txt"
    //输出分类器
    val outputClassifier = "E:\\编程\\kotlin\\images\\test.txt"
    //输出模型
    val outputModel = "E:\\编程\\kotlin\\images\\outputModel.txt"
    Train.train(imgBase, categories, cateSample, outputArff, outputClassifier, outputModel)
}

fun text(){
    //    val path = "D:\\images"
//    var images: MutableList<BufferedImage> = mutableListOf()
//    val fileTree: FileTreeWalk = File(path).walk()
//    fileTree.maxDepth(1) //需遍历的目录层级为1，即无需检查子目录
//        .filter {
//            it.isFile
//        } //只挑选文件，不处理文件夹
//        .filter {
//            it.extension in listOf("png", "jpg")
//        } //选择扩展名为png和jpg的图片文件
//        .forEach {
//            images.add(ImageIO.read(it))
//        } //循环处理符合条件的文件
}