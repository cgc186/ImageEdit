package bow

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


fun main() {
    val path = "D:\\images"
    var images: MutableList<BufferedImage> = mutableListOf()
    val fileTree: FileTreeWalk = File(path).walk()
    fileTree.maxDepth(1) //需遍历的目录层级为1，即无需检查子目录
        .filter {
            it.isFile
        } //只挑选文件，不处理文件夹
        .filter {
            it.extension in listOf("png", "jpg")
        } //选择扩展名为png和jpg的图片文件
        .forEach {
            images.add(ImageIO.read(it))
        } //循环处理符合条件的文件


}