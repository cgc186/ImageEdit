package kui.menu.drawingBoard

import java.awt.BorderLayout
import java.awt.GridLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel


object View : JFrame() {

    private const val serialVersionUID = 1L //序列化

    //获取画板
    var panPanel: JPanel = JPanel()
    //按钮区
    var btnsPanel: JPanel = JPanel()

    var cleBtn: JButton
    //功能按钮
    var rgnBtn: JButton

    //获取按钮
    fun getButton(btnName: String?): JButton? {
        when (btnName) {
            "清空画板" -> return cleBtn
            "识别" -> return rgnBtn
            else -> {
            }
        }
        return null
    }

    //构造函数
    init { //画图区
        //按钮区
        btnsPanel.layout = GridLayout(2, 1, 0, 10)
        rgnBtn = JButton("识别")
        cleBtn = JButton("清空画板")
        btnsPanel.add(rgnBtn)
        btnsPanel.add(cleBtn)
        //主界面
        contentPane.add(btnsPanel, BorderLayout.EAST)
        contentPane.add(panPanel, BorderLayout.CENTER)
        //设置窗体属性
        title = "手写数字识别板"
        this.setSize(480, 400)
        setLocationRelativeTo(null)
        this.isResizable = false
        defaultCloseOperation = EXIT_ON_CLOSE
        this.isVisible = false
    }
}