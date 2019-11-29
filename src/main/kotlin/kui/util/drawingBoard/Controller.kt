package kui.util.drawingBoard

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent


class Controller : ActionListener {
    //事件处理方法
    override fun actionPerformed(e: ActionEvent) {
        if (e.source === View.getButton("清空画板")) {
            Model.clearPanel()
        } else if (e.source === View.getButton("识别")) {
            Model.recognition()
        }
    }

    //构造方法
    init { //添加事件监听
        val btnNames = arrayOf("清空画板", "识别") //按钮名
        for (btnName in btnNames) {
            View.getButton(btnName)!!.addActionListener(this as ActionListener)
        }
        //鼠标按下事件
        View.panPanel.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                Model.mousePressed(e)
            }
        })
        //鼠标拖动事件，自由画图
        View.panPanel.addMouseMotionListener(object : MouseAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                Model.mouseDragged(e)
            }
        })
    }
}