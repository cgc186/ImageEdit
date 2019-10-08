package second.edgeDetection

import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.HeadlessException
import java.awt.Transparency
import java.awt.GraphicsEnvironment
import javax.swing.ImageIcon


object ImageUtil {
    public fun toGray(img: BufferedImage): BufferedImage {
        val grayImage = BufferedImage(img.width, img.height, BufferedImage.TYPE_BYTE_GRAY)
        for (i in 0 until img.width) {
            for (j in 0 until img.height) {
                val rgb = img.getRGB(i, j)
                grayImage.setRGB(i, j, rgb)
            }
        }
        return grayImage
    }

    private fun toBufferedImage(image: Image): BufferedImage {
        var image = image
        if (image is BufferedImage) {
            return image
        }
        image = ImageIcon(image).image
        var bimage: BufferedImage? = null
        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        try {
            val transparency = Transparency.OPAQUE
            val gs = ge.defaultScreenDevice
            val gc = gs.defaultConfiguration
            bimage = gc.createCompatibleImage(
                image.getWidth(null), image.getHeight(null), transparency
            )
        } catch (e: HeadlessException) {
        }

        if (bimage == null) {
            val type = BufferedImage.TYPE_INT_RGB
            bimage = BufferedImage(image.getWidth(null), image.getHeight(null), type)
        }
        val g = bimage.createGraphics()

        g.drawImage(image, 0, 0, null)
        g.dispose()
        return bimage
    }
}