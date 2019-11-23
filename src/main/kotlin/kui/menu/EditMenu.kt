package kui.menu

import first.gaussian.GaussianDao
import first.histogram.HistogramDao
import kui.ImageJFrame
import second.edgeDetection.EdgeDetectionDao
import second.hough.HoughDao
import javax.swing.ImageIcon
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.JOptionPane

object EditMenu {
    /**
     * 编辑菜单初始化
     *
     * @param editMenu
     */
    fun initEditMenu(editMenu: JMenu) { /*
         * 创建 "编辑" 一级菜单的子菜单
         */
        val drynessMenuItem = JMenuItem("去燥")
        val histogramEqualizationMenuItem = JMenuItem("直方图均衡化")
        val edgeDetectionMenuItem = JMenuItem("边缘检测")
        val houghLineMenuItem = JMenuItem("直线检测")
        // 子菜单添加到一级菜单
        editMenu.add(drynessMenuItem)
        editMenu.add(histogramEqualizationMenuItem)
        editMenu.add(edgeDetectionMenuItem)
        editMenu.add(houghLineMenuItem)
        // 设置 "去燥" 子菜单被点击的监听器
        drynessMenuItem.addActionListener {
            println("去燥  被点击")
            ImageJFrame.editImage = GaussianDao.gaussianEdit(ImageJFrame.imagePath!!)
            ImageJFrame.editImageIcon = ImageIcon(ImageJFrame.editImage)
            ImageJFrame.setImageIcon(ImageJFrame.editImageIcon!!, ImageJFrame.editImageJLabel!!, ImageJFrame.myPanel2)
        }
        // 设置 "直方图均衡化" 子菜单被点击的监听器
        histogramEqualizationMenuItem.addActionListener {
            println("直方图均衡化  被点击")
            ImageJFrame.editImage = HistogramDao.histogramEdit(ImageJFrame.imagePath!!)
            ImageJFrame.editImageIcon = ImageIcon(ImageJFrame.editImage)
            ImageJFrame.setImageIcon(ImageJFrame.editImageIcon!!, ImageJFrame.editImageJLabel!!, ImageJFrame.myPanel2)
        }
        // 设置 "边缘检测" 子菜单被点击的监听器
        edgeDetectionMenuItem.addActionListener {
            println("边缘检测  被点击")
            val options = arrayOf<Any>("sobel1", "sobel2", "sobel3")
            val response = JOptionPane.showOptionDialog(
                null,
                "这是个选项对话框，用户可以选择自己的按钮的个数",
                "选项对话框标题",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            )
            if (response == 0) {
                ImageJFrame.editImage = if (ImageJFrame.editImage != null) {
                    EdgeDetectionDao.sobel1ByImage(ImageJFrame.editImage!!)
                } else {
                    EdgeDetectionDao.sobel1(ImageJFrame.imagePath!!)
                }
            } else if (response == 1) {
                ImageJFrame.editImage = if (ImageJFrame.editImage != null) {
                    EdgeDetectionDao.sobel2ByImage(ImageJFrame.editImage!!)
                } else {
                    EdgeDetectionDao.sobel2(ImageJFrame.imagePath!!)
                }
            } else if (response == 2) {
                ImageJFrame.editImage = if (ImageJFrame.editImage != null) {
                    EdgeDetectionDao.sobel3ByImage(ImageJFrame.editImage!!)
                } else {
                    EdgeDetectionDao.sobel3(ImageJFrame.imagePath!!)
                }
            }
            ImageJFrame.editImageIcon = ImageIcon(ImageJFrame.editImage)
            ImageJFrame.setImageIcon(ImageJFrame.editImageIcon!!, ImageJFrame.editImageJLabel!!, ImageJFrame.myPanel2)
            ImageJFrame.isEdge = true
        }
        // 设置 "直线检测" 子菜单被点击的监听器
        houghLineMenuItem.addActionListener {
            println("直线检测  被点击")
            //editImage = HoughDao.INSTANCE.houghEdit(imagePath);
            val options = arrayOf<Any>("方案1", "方案2", "方案3")
            val response = JOptionPane.showOptionDialog(
                null,
                "这是个选项对话框，用户可以选择自己的按钮的个数",
                "选项对话框标题",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            )
            if (ImageJFrame.isEdge) {
                if (response == 0) {
                    ImageJFrame.editImage = HoughDao.houghEditByImage(1, ImageJFrame.editImage!!)
                } else if (response == 1) {
                    ImageJFrame.editImage = HoughDao.houghEditByImage(2, ImageJFrame.editImage!!)
                } else if (response == 2) {
                    ImageJFrame.editImage = HoughDao.houghEditByImage(3, ImageJFrame.editImage!!)
                }
                ImageJFrame.editImageIcon = ImageIcon(ImageJFrame.editImage)
                ImageJFrame.setImageIcon(
                    ImageJFrame.editImageIcon!!,
                    ImageJFrame.editImageJLabel!!,
                    ImageJFrame.myPanel2
                )
            } else {
                if (response == 0) {
                    ImageJFrame.editImage = HoughDao.houghEdit(1, ImageJFrame.imagePath!!)
                } else if (response == 1) {
                    ImageJFrame.editImage = HoughDao.houghEdit(2, ImageJFrame.imagePath!!)
                } else if (response == 2) {
                    ImageJFrame.editImage = HoughDao.houghEdit(3, ImageJFrame.imagePath!!)
                }
                ImageJFrame.editImageIcon = ImageIcon(ImageJFrame.editImage)
                ImageJFrame.setImageIcon(
                    ImageJFrame.editImageIcon!!,
                    ImageJFrame.editImageJLabel!!,
                    ImageJFrame.myPanel2
                )
            }
        }
    }
}