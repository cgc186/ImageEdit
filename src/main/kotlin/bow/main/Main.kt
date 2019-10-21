package bow.main

fun main() {
    //图片路径
    val imgBase = "E:\\编程\\kotlin\\images\\training"
    //类别
    val categories = arrayOf("Phoning", "PlayingGuitar")

    //("Phoning", "PlayingGuitar", "RidingBike", "RidingHorse", "Running", "Shooting")

    //实物样本数量
    val cateSample = 16
    //输出路径
    val outputArff = "E:\\编程\\kotlin\\images\\train.txt"
    //输出分类器
    val outputClassifier = "E:\\编程\\kotlin\\images\\train.txt"
    //输出模型
    val outputModel = "E:\\编程\\kotlin\\images\\outputModel.txt"
    Train.train(imgBase, categories, cateSample, outputArff, outputClassifier, outputModel)
}
