package kui.menu

import javax.swing.JMenu
import javax.swing.JMenuItem

object BowMenu {
    /**
     * 图像检索菜单初始化
     *
     * @param bowMenu
     */
    fun initBowMenu(bowMenu: JMenu) { /*
         * 创建 "文件" 一级菜单的子菜单
         */
        val trainMenuItem = JMenuItem("训练")
        val testMenuItem = JMenuItem("测试")
        val categoryMenuItem = JMenuItem("识别")
        // 子菜单添加到一级菜单
        bowMenu.add(trainMenuItem)
        bowMenu.addSeparator() // 添加一条分割线
        bowMenu.add(testMenuItem)
        bowMenu.add(categoryMenuItem)
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