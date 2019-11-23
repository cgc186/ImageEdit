package kui

import kui.menu.Menu
import java.awt.Image
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.image.BufferedImage
import javax.swing.*


object ImageJFrame {
    val jf = JFrame("图像处理")
    var menuBar: JMenuBar? = null
    var jSplitPane: JSplitPane? = null
    val myPanel1 = JPanel() //面板1
    val myPanel2 = JPanel() //面板2
    var imageJLabel: JLabel? = null
    var editImageJLabel: JLabel? = null
    var imageIcon: ImageIcon? = null
    var imagePath: String? = null
    var editImageIcon: ImageIcon? = null
    var editImage: BufferedImage? = null
    var isEdge = false

    val jMenuBar: JMenuBar
        get() = Menu.menuBar

    init {
        menuBar = Menu.menuBar
        jSplitPane = MyJSplitPane.getJSplitPane()
    }

    fun setImageIcon(icon: ImageIcon, jLabel: JLabel, panel: JPanel) {
        jLabel.icon = ImageIcon(resize(icon, panel))
        panel.isVisible = true
        panel.updateUI()
    }

    /**
     * 窗口变化监听器
     */
    fun resizeListener() {
        jf.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                super.componentResized(e)
                val jfWidth = jf.width
                val jfHeight = jf.height
                myPanel1.setSize(jfWidth / 2, jfHeight - menuBar!!.height)
                myPanel2.setSize(jfWidth / 2, jfHeight - menuBar!!.height)
                jSplitPane!!.dividerLocation = jfWidth / 2

                if (imageIcon != null) {
                    imageJLabel!!.icon = ImageIcon(resize(imageIcon!!, myPanel1))
                }
                if (editImageIcon != null) {
                    editImageJLabel!!.icon = ImageIcon(resize(editImageIcon!!, myPanel2))
                }
            }
        })
    }

    /**
     * 图片跟随窗口大小发生改变
     *
     * @param img
     * @param jp
     * @return
     */
    private fun resize(img: ImageIcon, jp: JPanel): Image {
        val imgWidth = img.iconWidth
        val imgHeight = img.iconHeight
        val conWidth = jp.width
        val conHeight = jp.height
        val reImgWidth: Int
        val reImgHeight: Int
        if (imgWidth.toDouble() / imgHeight.toDouble() >= conWidth.toDouble() / conHeight.toDouble()) {
            if (imgWidth > conWidth) {
                reImgWidth = conWidth
                reImgHeight = imgHeight * reImgWidth / imgWidth
            } else {
                reImgWidth = imgWidth
                reImgHeight = imgHeight
            }
        } else {
            if (imgWidth > conWidth) {
                reImgHeight = conHeight
                reImgWidth = imgWidth * reImgHeight / imgHeight
            } else {
                reImgWidth = imgWidth
                reImgHeight = imgHeight
            }
        }
        return img.image.getScaledInstance(reImgWidth, reImgHeight, Image.SCALE_DEFAULT)
    }


}