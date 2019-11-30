package kui.menu

import bow.BowDao
import kui.ImageJFrame
import kui.ImageJFrame.imagePath
import kui.util.MenuUtil
import second.edgeDetection.EdgeDetectionDao
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*

object BowMenu {

    //训练目录
    var dataFolder = "data/bowData/"
    //训练集位置
    var trainFolder: String = ""

    var templateFolder = dataFolder + "templates/"
    var testFolder = dataFolder + "test_image"
    var resultFolder = dataFolder + "result_image/"

    private fun initFolder() {
        templateFolder = dataFolder + "templates/"
        testFolder = dataFolder + "test_image"
        resultFolder = dataFolder + "result_image/"
    }

    /**
     * 图像检索菜单初始化
     *
     * @param bowMenu
     */
    fun initBowMenu(bowMenu: JMenu) { /*
         * 创建 "图像检索" 一级菜单的子菜单
         */
        val trainMenu = JMenu("训练")
        val testMenuItem = JMenu("测试")
        val categoryMenuItem = JMenuItem("识别")
        // 子菜单添加到一级菜单
        bowMenu.add(trainMenu)
        bowMenu.addSeparator() // 添加一条分割线
        bowMenu.add(testMenuItem)
        bowMenu.add(categoryMenuItem)

        val setTrainMenuItem = JMenuItem("设置训练目录")
        val startTrainMenuItem = JMenuItem("开始训练")
        val resultMenuItem = JMenuItem("查看训练结果")

        trainMenu.add(startTrainMenuItem)
        trainMenu.add(setTrainMenuItem)
        trainMenu.add(resultMenuItem)

        val setTest = JMenuItem("设置测试目录")
        val startTestMenuItem = JMenuItem("开始测试")

        testMenuItem.add(setTest)
        testMenuItem.add(startTestMenuItem)

        /*
         * 菜单项的点击/状态改变事件监听，事件监听可以直接设置在具体的子菜单上
         */

        // 设置 "设置训练目录" 子菜单被点击的监听器
        setTrainMenuItem.addActionListener {
            println("设置训练目录  被点击")
            trainFolder = MenuUtil.getPath()
            dataFolder = BowDao.getDataFolder(trainFolder)
            initFolder()
            println(trainFolder)
        }

        // 设置 "开始训练" 子菜单被点击的监听器
        startTrainMenuItem.addActionListener {
            println("开始训练  被点击")
            if (trainFolder.isNotEmpty()) {
                dataFolder = BowDao.getDataFolder(trainFolder)
                initFolder()
                BowDao.train(trainFolder, templateFolder, testFolder, resultFolder)
            }
            JOptionPane.showMessageDialog(
                null,
                "训练完成",
                "bow",
                JOptionPane.INFORMATION_MESSAGE
            )
        }

        // 设置 "查看训练结果" 子菜单被点击的监听器
        resultMenuItem.addActionListener {
            println("查看训练结果  被点击")
            val f = File(dataFolder, "/svm/")
            Runtime.getRuntime().exec("explorer \"${f.absolutePath}\"")
        }

        // 设置 "设置测试目录" 子菜单被点击的监听器
        setTest.addActionListener {
            println("设置测试目录  被点击")
            testFolder = MenuUtil.getPath()
            dataFolder = BowDao.getDataFolder(testFolder)
            initFolder()
        }
        // 设置 "开始测试" 子菜单被点击的监听器
        startTestMenuItem.addActionListener {
            println("开始测试  被点击")
            val testList = BowDao.getTestList(testFolder)
            var yes = 0
            var total = 0
            testList.forEach {
                imagePath = it
                ImageJFrame.imageIcon = ImageIcon(imagePath)
                ImageJFrame.setImageIcon(ImageJFrame.imageIcon!!, ImageJFrame.imageJLabel!!, ImageJFrame.myPanel1)
                imagePath?.let { it1 ->
                    val categoryImage = BowDao.categoryImage(it1, dataFolder)
                    println(categoryImage)
                    val templateImage = BowDao.getTemplateImage(categoryImage, templateFolder)
                    println(templateImage)
                    ImageJFrame.editImage = ImageIO.read(File(templateImage))
                    ImageJFrame.editImageIcon = ImageIcon(ImageJFrame.editImage)
                    ImageJFrame.setImageIcon(
                        ImageJFrame.editImageIcon!!,
                        ImageJFrame.editImageJLabel!!,
                        ImageJFrame.myPanel2
                    )
                }
                val options = arrayOf<Any>("正确", "错误")
                val response = JOptionPane.showOptionDialog(
                    null,
                    "该图片是否检测正确?",
                    "图像测试",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
                )
                total++
                if (response == 0) {
                    yes++
                }
            }
            println("正确次数:$yes")
            val options = arrayOf<Any>("确定")
            JOptionPane.showOptionDialog(
                null,
                "正确率${(yes.toDouble()) / (total.toDouble())}",
                "图像测试",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            )
        }

        // 设置 "识别" 子菜单被点击的监听器
        categoryMenuItem.addActionListener {
            println("识别  被点击")
            if (dataFolder.isNotEmpty()) {
                println("识别  被点击")
                imagePath?.let { it1 ->
                    val categoryImage = BowDao.categoryImage(it1, dataFolder)
                    println(categoryImage)
                    val templateImage = BowDao.getTemplateImage(categoryImage, templateFolder)
                    println(templateImage)
                    ImageJFrame.editImage = ImageIO.read(File(templateImage))
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

}