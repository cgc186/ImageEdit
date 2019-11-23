package second.edgeDetection

import java.awt.FileDialog
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFrame
import kotlin.system.exitProcess

fun main() {
    val edgeDetection= EdgeDetection()
    val jf = JFrame()
    val fd = FileDialog(jf,"选择图片")
    fd.isVisible = true
    val img = ImageIO.read(File(fd.directory,fd.file))
    val resulting = edgeDetection.sobel2(img)
//    val resulting = ImageUtil.toGray(img)
    val output = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_BGR)
    output.createGraphics().drawImage(resulting, 0, 0, null)
    val fd2 = FileDialog(jf,"储存图片")
    fd2.mode = FileDialog.SAVE
    fd2.isVisible = true
    ImageIO.write(resulting,"jpg",File(fd2.directory,fd2.file))
    exitProcess(0)
}