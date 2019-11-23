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
        val trainMenuItem = JMenuItem("训练")
        val testMenuItem = JMenuItem("测试")
        val categoryMenuItem = JMenuItem("识别")
        // 子菜单添加到一级菜单
        numberMenu.add(trainMenuItem)
        numberMenu.addSeparator() // 添加一条分割线
        numberMenu.add(testMenuItem)
        numberMenu.add(categoryMenuItem)
        /*
         * 菜单项的点击/状态改变事件监听，事件监听可以直接设置在具体的子菜单上
         */
        // 设置 "训练" 子菜单被点击的监听器
        trainMenuItem.addActionListener { println("训练  被点击") }
        // 设置 "测试" 子菜单被点击的监听器
        testMenuItem.addActionListener { println("测试  被点击") }
        // 设置 "识别" 子菜单被点击的监听器
        categoryMenuItem.addActionListener { println("识别  被点击") }
    }
}