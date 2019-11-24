package bow

fun main() {
    val trainFolder = "E:\\coding\\kotlin\\ImageEdit\\data\\bowData\\train_images"
    val number = BowDao.getDataFolder(trainFolder)
    println(number)
}