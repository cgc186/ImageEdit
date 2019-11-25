package kui.util

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
}