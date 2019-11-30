package kui.util

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser

object MenuUtil {
    fun getPath(): String {
        val fileChooser = JFileChooser("data")
        fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        val returnVal = fileChooser.showOpenDialog(fileChooser)
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fileChooser.selectedFile.absolutePath //这个就是你选择的文件夹的路径
        }
        return ""
    }

    fun getImageName(namePath: String, type: String): String {
        val list = namePath.split(type)
        list.forEach { println(it) }
        return list[list.size - 1]
    }

    fun getImg(path: String): BufferedImage? {
        return ImageIO.read(File(path))
    }
}