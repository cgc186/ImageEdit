package kui.menu

import bpMnist.MnistDao
import cnn.CnnDao
import kui.menu.drawingBoard.Controller
import kui.menu.drawingBoard.Model
import kui.menu.drawingBoard.View
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
        val trainMenuItem = JMenu("训练")
        val modelMenuItem = JMenuItem("打开手写板")

        // 子菜单添加到一级菜单
        numberMenu.add(trainMenuItem)
        numberMenu.add(modelMenuItem)

        val startCnnTrainMenuItem = JMenuItem("开始训练")

        trainMenuItem.add(startCnnTrainMenuItem)

        /*
         * 菜单项的点击/状态改变事件监听，事件监听可以直接设置在具体的子菜单上
         */
        // 设置 "开始训练" 子菜单被点击的监听器
        startCnnTrainMenuItem.addActionListener {
            println("开始训练  被点击")
            CnnDao.train();
        }

        // 设置 "打开手写板" 子菜单被点击的监听器
        modelMenuItem.addActionListener {
            println("打开手写板  被点击")
            View.isVisible = true
            Controller() //控制器
        }
    }
}