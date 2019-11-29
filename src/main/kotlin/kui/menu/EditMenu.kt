package kui.menu

import caffe.CaffeDao
import edit.EditDao
import first.gaussian.GaussianDao
import first.histogram.HistogramDao
import kui.ImageJFrame
import kui.util.MenuUtil
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
        val featuresMenuItem = JMenuItem("特征点检测")
        val pcaMenuItem = JMenuItem("主成分分析")
        val imageClusterMenuItem = JMenuItem("图像分割");
        val itemMenuItem = JMenuItem("物体检测")
        val itemNameMenuItem = JMenuItem("物体识别")


        // 子菜单添加到一级菜单
        editMenu.add(drynessMenuItem)
        editMenu.add(histogramEqualizationMenuItem)
        editMenu.add(edgeDetectionMenuItem)
        editMenu.add(houghLineMenuItem)
        editMenu.add(featuresMenuItem)
        editMenu.add(pcaMenuItem)
        editMenu.add(imageClusterMenuItem)
        editMenu.add(itemMenuItem)
        editMenu.add(itemNameMenuItem)
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
                when (response) {
                    0 -> {
                        ImageJFrame.editImage = HoughDao.houghEditByImage(1, ImageJFrame.editImage!!)
                    }
                    1 -> {
                        ImageJFrame.editImage = HoughDao.houghEditByImage(2, ImageJFrame.editImage!!)
                    }
                    2 -> {
                        ImageJFrame.editImage = HoughDao.houghEditByImage(3, ImageJFrame.editImage!!)
                    }
                }
                ImageJFrame.editImageIcon = ImageIcon(ImageJFrame.editImage)
                ImageJFrame.setImageIcon(
                    ImageJFrame.editImageIcon!!,
                    ImageJFrame.editImageJLabel!!,
                    ImageJFrame.myPanel2
                )
            } else {
                when (response) {
                    0 -> {
                        ImageJFrame.editImage = HoughDao.houghEdit(1, ImageJFrame.imagePath!!)
                    }
                    1 -> {
                        ImageJFrame.editImage = HoughDao.houghEdit(2, ImageJFrame.imagePath!!)
                    }
                    2 -> {
                        ImageJFrame.editImage = HoughDao.houghEdit(3, ImageJFrame.imagePath!!)
                    }
                }
                ImageJFrame.editImageIcon = ImageIcon(ImageJFrame.editImage)
                ImageJFrame.setImageIcon(
                    ImageJFrame.editImageIcon!!,
                    ImageJFrame.editImageJLabel!!,
                    ImageJFrame.myPanel2
                )
            }
        }
        // 设置 "特征点检测" 子菜单被点击的监听器
        featuresMenuItem.addActionListener {
            println("特征点检测  被点击")
            //editImage = HoughDao.INSTANCE.houghEdit(imagePath);
            val options = arrayOf<Any>("方案1", "方案2")
            val response = JOptionPane.showOptionDialog(
                null,
                "特征点检测方案",
                "特征点检测",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            )
            ImageJFrame.imagePath?.let { path ->
                var savePath = MenuUtil.getPath()
                if (response == 0) {
                    savePath += "/s" + MenuUtil.getImageName(path,"\\")
                    EditDao.surf(path, savePath)
                    ImageJFrame.editImage = MenuUtil.getImg(savePath)
                } else if (response == 1) {
                    savePath += "/ch" + MenuUtil.getImageName(path,"\\")
                    EditDao.cornerHairrs(path, savePath)
                    ImageJFrame.editImage = MenuUtil.getImg(savePath)
                }
                ImageJFrame.editImageIcon = ImageIcon(ImageJFrame.editImage)
                ImageJFrame.setImageIcon(
                    ImageJFrame.editImageIcon!!,
                    ImageJFrame.editImageJLabel!!,
                    ImageJFrame.myPanel2
                )
            }
        }
        // 设置 "主成分分析" 子菜单被点击的监听器
        pcaMenuItem.addActionListener {
            println("主成分分析  被点击")

            ImageJFrame.imagePath?.let { path ->
                var savePath = MenuUtil.getPath()
                savePath += "/pca" + MenuUtil.getImageName(path,"\\")
                EditDao.getPca(path, savePath)
                ImageJFrame.editImage = MenuUtil.getImg(savePath)
                ImageJFrame.editImageIcon = ImageIcon(ImageJFrame.editImage)
                ImageJFrame.setImageIcon(
                    ImageJFrame.editImageIcon!!,
                    ImageJFrame.editImageJLabel!!,
                    ImageJFrame.myPanel2
                )
            }
        }
        // 设置 "图像分割" 子菜单被点击的监听器
        imageClusterMenuItem.addActionListener {
            println("图像分割  被点击")
            //editImage = HoughDao.INSTANCE.houghEdit(imagePath);
            val options = arrayOf<Any>("方案1", "方案2")
            val response = JOptionPane.showOptionDialog(
                null,
                "图像分割方案",
                "图像分割",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            )
            ImageJFrame.imagePath?.let { path ->
                var savePath = MenuUtil.getPath()
                if (response == 0) {
                    savePath += "/t1" + MenuUtil.getImageName(path,"\\")
                    EditDao.imageCluster(path, savePath,1)
                    ImageJFrame.editImage = MenuUtil.getImg(savePath)
                } else if (response == 1) {
                    savePath += "/t2" + MenuUtil.getImageName(path,"\\")
                    EditDao.imageCluster(path, savePath,2)
                    ImageJFrame.editImage = MenuUtil.getImg(savePath)
                }
                ImageJFrame.editImageIcon = ImageIcon(ImageJFrame.editImage)
                ImageJFrame.setImageIcon(
                    ImageJFrame.editImageIcon!!,
                    ImageJFrame.editImageJLabel!!,
                    ImageJFrame.myPanel2
                )
            }
        }
        // 设置 "物体检测" 子菜单被点击的监听器
        itemMenuItem.addActionListener {
            println("物体检测  被点击")
            //editImage = HoughDao.INSTANCE.houghEdit(imagePath);

            ImageJFrame.imagePath?.let { path ->
                var savePath = MenuUtil.getPath()
                savePath += "/Caffe" + MenuUtil.getImageName(path,"\\")
                CaffeDao.caffe(path, savePath)
                ImageJFrame.editImage = MenuUtil.getImg(savePath)
                ImageJFrame.editImageIcon = ImageIcon(ImageJFrame.editImage)
                ImageJFrame.setImageIcon(
                    ImageJFrame.editImageIcon!!,
                    ImageJFrame.editImageJLabel!!,
                    ImageJFrame.myPanel2
                )
            }
        }
        // 设置 "物体识别" 子菜单被点击的监听器
        itemNameMenuItem.addActionListener {
            println("物体识别  被点击")

            ImageJFrame.imagePath?.let { path ->
                val itemType = CaffeDao.getItemType(path)
                val options = arrayOf<Any>("确定")
                val response = JOptionPane.showOptionDialog(
                    null,
                    "该物体为$itemType",
                    "物体识别",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
                )
            }
        }
    }
}