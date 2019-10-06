package gaussian

import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.awt.FileDialog
import javax.swing.JFrame
import kotlin.system.exitProcess


fun main() {
    val gaussian: Gaussian = Gaussian()
    val jf = JFrame()
    val fd = FileDialog(jf,"选择图片")
    fd.show()
    val img = ImageIO.read(File(fd.directory,fd.file))
    val resulting = gaussian.myGaussianFilter(img, 20, 1.5f)
    val output = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_BGR)
    output.createGraphics().drawImage(resulting, 0, 0, null)
    val fd2 = FileDialog(jf,"储存图片")
    fd2.mode = FileDialog.SAVE
    fd2.show()
    ImageIO.write(resulting,"jpg",File(fd2.directory,fd2.file))
    exitProcess(0)
}