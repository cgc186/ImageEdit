package kui.menu

import kui.ImageJFrame
import kui.util.MenuUtil
import lda.LdaDao
import lda.LdaDao.faceModelPath
import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.JOptionPane

object LdaMenu {
    private var ldaFolder = ""

    /**
     * 人脸图像识别菜单初始化
     *
     * @param ldaMenu
     */
    fun initLdaMenu(ldaMenu: JMenu) { /*
         * 创建 "文件" 一级菜单的子菜单
         */
        val setLdaTrainMenuItem = JMenuItem("设置图像集目录")
        val ldaTrainMenu = JMenu("训练")
        val ldaTestMenuItem = JMenu("测试")
        val predictMenuItem = JMenuItem("识别")
        // 子菜单添加到一级菜单
        ldaMenu.add(setLdaTrainMenuItem)
        ldaMenu.add(ldaTrainMenu)
        ldaMenu.addSeparator() // 添加一条分割线
        ldaMenu.add(ldaTestMenuItem)
        ldaMenu.add(predictMenuItem)


        val startLdaTrainMenuItem = JMenuItem("开始训练")
        val ldaResultMenuItem = JMenuItem("查看训练结果")

        ldaTrainMenu.add(startLdaTrainMenuItem)
        ldaTrainMenu.add(ldaResultMenuItem)

        val startTestMenuItem = JMenuItem("开始测试")

        ldaTestMenuItem.add(startTestMenuItem)

        /*
         * 菜单项的点击/状态改变事件监听，事件监听可以直接设置在具体的子菜单上
         */
        // 设置 "设置训练目录" 子菜单被点击的监听器
        setLdaTrainMenuItem.addActionListener {
            println("设置训练目录  被点击")
            ldaFolder = MenuUtil.getPath()
            println(ldaFolder)
            LdaDao.initFolderList(ldaFolder, 1)
        }

        // 设置 "开始训练" 子菜单被点击的监听器
        startLdaTrainMenuItem.addActionListener {
            println("开始训练  被点击")
            LdaDao.train()
            JOptionPane.showMessageDialog(
                null,
                "训练完成",
                "人脸检测",
                JOptionPane.INFORMATION_MESSAGE
            )
        }

        // 设置 "查看训练结果" 子菜单被点击的监听器
        ldaResultMenuItem.addActionListener {
            println("查看训练结果  被点击")
            if (faceModelPath.isNotEmpty()) {
                val f = File(faceModelPath)
                Runtime.getRuntime().exec("explorer \"${f.absolutePath}\"")
            }
        }

        // 设置 "开始测试" 子菜单被点击的监听器
        startTestMenuItem.addActionListener {
            println("开始测试  被点击")
            LdaDao.initTest()
            var yes = 0  //记录正确个数
            var total = 0  //记录总数

            LdaDao.testList.forEach {
                val predict = LdaDao.predict(it.key)
                val template = LdaDao.getTemplate(predict)
                total++
                if (LdaDao.isRight(it.key, predict)) {
                    yes++
                }
                ImageJFrame.imagePath = it.key
                ImageJFrame.imageIcon = ImageIcon(ImageJFrame.imagePath)
                ImageJFrame.setImageIcon(ImageJFrame.imageIcon!!, ImageJFrame.imageJLabel!!, ImageJFrame.myPanel1)
                ImageJFrame.editImage = ImageIO.read(File(template))
                ImageJFrame.editImageIcon = ImageIcon(ImageJFrame.editImage)
                ImageJFrame.setImageIcon(
                    ImageJFrame.editImageIcon!!,
                    ImageJFrame.editImageJLabel!!,
                    ImageJFrame.myPanel2
                )
                val options = arrayOf<Any>("确定")
                JOptionPane.showOptionDialog(
                    null,
                    "查看下一次",
                    "人脸检测",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
                )
            }
            val options = arrayOf<Any>("确定")
            JOptionPane.showOptionDialog(
                null,
                "正确率${(yes.toDouble()) / (total.toDouble())}",
                "人脸识别测试",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            )
        }

        // 设置 "识别" 子菜单被点击的监听器
        predictMenuItem.addActionListener {
            println("识别  被点击")
            ImageJFrame.imagePath?.let { it1 ->
                val predict = LdaDao.predict(it1)
                val template = LdaDao.getTemplate(predict)
                //println(template)
                ImageJFrame.editImage = ImageIO.read(File(template))
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