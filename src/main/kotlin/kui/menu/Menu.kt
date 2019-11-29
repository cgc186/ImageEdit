package kui.menu

import javax.swing.*

object Menu {
    val menuBar by lazy { JMenuBar() }

    init {
        val fileMenu = JMenu("文件")
        val editMenu = JMenu("编辑")
        val bowMenu = JMenu("物体识别")
        val ldaMenu = JMenu("人脸图像识别")
        val numberMenu = JMenu("手写数字识别")

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

        menuBar.add(aboutMenu)
    }
}