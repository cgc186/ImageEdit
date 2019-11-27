package kui.menu

import javax.swing.JMenu
import javax.swing.JMenuItem

object NumberMenu {
    /**
     * 图像检索菜单初始化
     *
     * @param numberMenu
     */
    fun initNumberMenu(numberMenu: JMenu) { /*
         * 创建 "文件" 一级菜单的子菜单
         */
        //JMenuItem newMenuItem = new JMenuItem("新建");
        val trainMenuItem = JMenu("训练")
        val testMenuItem = JMenuItem("测试")
        val categoryMenuItem = JMenuItem("识别")
        // 子菜单添加到一级菜单
        numberMenu.add(trainMenuItem)
        numberMenu.addSeparator() // 添加一条分割线
        numberMenu.add(testMenuItem)
        numberMenu.add(categoryMenuItem)


        val startCnnTrainMenuItem = JMenuItem("开始训练")
        val cnnResultMenuItem = JMenuItem("查看训练结果")

        trainMenuItem.add(startCnnTrainMenuItem)
        trainMenuItem.add(cnnResultMenuItem)

        //val setTest = JMenuItem("设置测试目录")
        val startTestMenuItem = JMenuItem("开始测试")

        //ldaTestMenuItem.add(setTest)
        testMenuItem.add(startTestMenuItem)

        /*
         * 菜单项的点击/状态改变事件监听，事件监听可以直接设置在具体的子菜单上
         */
        // 设置 "设置训练目录" 子菜单被点击的监听器
        startCnnTrainMenuItem.addActionListener {
//            println("设置训练目录  被点击")
//            ldaFolder = MenuUtil.getPath()
//            LdaDao.initFolderList(ldaFolder, 1)
        }

        // 设置 "开始训练" 子菜单被点击的监听器
        startCnnTrainMenuItem.addActionListener {
            println("开始训练  被点击")
//            LdaDao.train()
        }

        // 设置 "查看训练结果" 子菜单被点击的监听器
        cnnResultMenuItem.addActionListener {
            println("查看训练结果  被点击")
//            if (faceModelPath.isNotEmpty()) {
//                val f = File(faceModelPath)
//                Runtime.getRuntime().exec("explorer \"${f.absolutePath}\"")
//            }
        }

        // 设置 "开始测试" 子菜单被点击的监听器
        startTestMenuItem.addActionListener {
            println("开始测试  被点击")

        }

    }
}