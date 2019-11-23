package kui.menu

import javax.swing.*

object Menu {
    val menuBar by lazy { JMenuBar() }

    init { /*
             * 创建一个菜单栏
             */
        /*
         * 创建一级菜单
         */
        val fileMenu = JMenu("文件")
        val editMenu = JMenu("编辑")
        val bowMenu = JMenu("物体识别")
        val ldaMenu = JMenu("人脸图像识别")
        val numberMenu = JMenu("手写数字识别")
        val viewMenu = JMenu("图像")
        val aboutMenu = JMenu("关于")
        // 一级菜单添加到菜单栏
        menuBar.add(fileMenu)
        FileMenu.initFileMenu(fileMenu)

        menuBar.add(editMenu)
        EditMenu.initEditMenu(editMenu)

        menuBar.add(bowMenu)
        BowMenu.initBowMenu(bowMenu)

        menuBar.add(ldaMenu)
        LdaMenu.initLdaMenu(ldaMenu)

        menuBar.add(numberMenu)
        NumberMenu.initNumberMenu(numberMenu)

        menuBar.add(viewMenu)
        menuBar.add(aboutMenu)
        /*
         * 创建 "视图" 一级菜单的子菜单
         */
        val checkBoxMenuItem = JCheckBoxMenuItem("复选框子菜单")
        val radioButtonMenuItem01 = JRadioButtonMenuItem("单选按钮子菜单01")
        val radioButtonMenuItem02 = JRadioButtonMenuItem("单选按钮子菜单02")
        // 子菜单添加到一级菜单
        viewMenu.add(checkBoxMenuItem)
        viewMenu.addSeparator() // 添加一个分割线
        viewMenu.add(radioButtonMenuItem01)
        viewMenu.add(radioButtonMenuItem02)
        // 其中两个 单选按钮子菜单，要实现单选按钮的效果，需要将它们放到一个按钮组中
        val btnGroup = ButtonGroup()
        btnGroup.add(radioButtonMenuItem01)
        btnGroup.add(radioButtonMenuItem02)
        // 默认第一个单选按钮子菜单选中
        radioButtonMenuItem01.isSelected = true
        // 设置 复选框子菜单 状态改变 监听器
        checkBoxMenuItem.addChangeListener { println("复选框是否被选中: " + checkBoxMenuItem.isSelected) }
        // 设置 单选按钮子菜单 状态改变 监听器
        radioButtonMenuItem01.addChangeListener { println("单选按钮01 是否被选中: " + radioButtonMenuItem01.isSelected) }
    }
}