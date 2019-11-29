package kui.menu

import kui.ImageJFrame
import kui.ImageJFrame.editImage
import kui.ImageJFrame.editImageIcon
import kui.ImageJFrame.editImageJLabel
import kui.ImageJFrame.imageIcon
import kui.ImageJFrame.imageJLabel
import kui.ImageJFrame.imagePath
import kui.ImageJFrame.isEdge
import kui.ImageJFrame.jf
import kui.ImageJFrame.myPanel1
import java.awt.FileDialog
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.*

object FileMenu {
    /**
     * 文件菜单初始化
     *
     * @param fileMenu
     */
    fun initFileMenu(fileMenu: JMenu) { /*
         * 创建 "文件" 一级菜单的子菜单
         */
        val openMenu = JMenu("打开")
        val exitMenuItem = JMenuItem("关闭")
        val saveMenuItem = JMenuItem("另存为")
        // 子菜单添加到一级菜单
        fileMenu.add(openMenu)
        fileMenu.addSeparator() // 添加一条分割线
        fileMenu.add(exitMenuItem)
        fileMenu.add(saveMenuItem)
        val imageMenuItem = JMenuItem("图片") //创建子菜单

        openMenu.add(imageMenuItem)
        /*
         * 菜单项的点击/状态改变事件监听，事件监听可以直接设置在具体的子菜单上
         */
        // 设置 "打开图片" 子菜单被点击的监听器
        imageMenuItem.addActionListener {
            println("打开图片  被点击")
            val fd = FileDialog(jf as JFrame, "选择图片")
            fd.isVisible = true
            imagePath = fd.directory + fd.file
            println(imagePath)
            imageIcon = ImageIcon(imagePath)
            ImageJFrame.setImageIcon(imageIcon!!, imageJLabel!!, myPanel1)
        }

        // 设置 "关闭" 子菜单被点击的监听器
        exitMenuItem.addActionListener {
            println("关闭  被点击")
            editImageIcon = null
            editImageJLabel!!.icon = null
            isEdge = false
            editImage = null
        }
        // 设置 "另存为" 子菜单被点击的监听器
        saveMenuItem.addActionListener {
            println("另存为  被点击")
            val fd2 = FileDialog(jf, "储存图片")
            fd2.mode = FileDialog.SAVE
            fd2.isVisible = true
            try {
                ImageIO.write(editImage, "jpg", File(fd2.directory, fd2.file))
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
    }

}