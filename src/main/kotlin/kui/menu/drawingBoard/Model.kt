package kui.menu.drawingBoard

import bpMnist.MnistDao
import cnn.CnnDao
import java.awt.*
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.io.File
import java.io.PrintWriter
import javax.swing.JOptionPane


object Model {
    private var view = View

    private var startX = 0
    private var startY = 0
    private var endX = 0
    private var endY //面板画图参数
            = 0//转换为灰度//分割为R\G\B//获取RGB值//获取长//获取宽

    //字节流转图片对象
    private fun getImage(): DoubleArray {
        //构建图片流
        var myImage: BufferedImage? = null
        myImage = Robot().createScreenCapture(
            Rectangle(
                view.x, view.y + 30,
                View.panPanel.size.width, View.panPanel.size.height
            )
        )
        //字节流转图片对象
        val bi: Image? = myImage
        //构建图片流
        val tag = BufferedImage(28, 28, BufferedImage.TYPE_INT_RGB)
        //绘制改变尺寸后的图
        tag.graphics.drawImage(bi, 0, 0, 28, 28, null)

        val w = tag.width //获取宽
        val h = tag.height //获取长

        val rgbArray = DoubleArray(w * h)
        for (i in 0 until w) {
            for (j in 0 until h) {
                val rgb = tag.getRGB(j, i) //获取RGB值
                val r = rgb and 0xff0000 shr 16 //分割为R\G\B
                val g = rgb and 0xff00 shr 8
                val b = rgb and 0xff
                val gray = (r + g + b) / 3 //转换为灰度
                rgbArray[i * w + j] = if (gray >= 255 / 2) 0.0 else 1.0
            }
        }
        return rgbArray
    }

    private fun getImageFile() {
        //构建图片流
        var myImage: BufferedImage? = null
        myImage = Robot().createScreenCapture(
            Rectangle(
                view.x, view.y + 30,
                View.panPanel.size.width, View.panPanel.size.height
            )
        )
        //字节流转图片对象
        val bi: Image? = myImage
        //构建图片流
        val tag = BufferedImage(28, 28, BufferedImage.TYPE_INT_RGB)
        //绘制改变尺寸后的图
        tag.graphics.drawImage(bi, 0, 0, 28, 28, null)

        val w = tag.width //获取宽
        val h = tag.height //获取长
        var rgbArray = String()
        for (i in 0 until w) {
            for (j in 0 until h) {
                val rgb = tag.getRGB(j, i) //获取RGB值
                val r = rgb and 0xff0000 shr 16 //分割为R\G\B
                val g = rgb and 0xff00 shr 8
                val b = rgb and 0xff
                val gray = (r + g + b) / 3 //转换为灰度
                rgbArray += if (gray >= 255 / 2) "0," else "1,"
            }
        }
        rgbArray = rgbArray.substring(0, rgbArray.length - 1)
        val writer = PrintWriter(File("data/mnist/test2.format"))
        writer.write(rgbArray)
        writer.flush()
        writer.close()
    }

    //识别
    fun recognition() {
        //val image = getImage()
        //val result = MnistDao.test(image)
        getImageFile()
        val predict = CnnDao.predict()

        JOptionPane.showMessageDialog(
            null,
            "识别结果是：$predict",
            "结果", JOptionPane.INFORMATION_MESSAGE
        )
    }

    //清空画板
    fun clearPanel() {
        val graphics: Graphics = View.panPanel.graphics
        graphics.clearRect(
            0,
            0, View.panPanel.size.width, View.panPanel.size.height
        ) //清空myPanel
    }

    //鼠标拖动，自由画图
    fun mouseDragged(e: MouseEvent) {
        val graphics: Graphics = View.panPanel.graphics
        //获取位置信息
        endX = e.x
        endY = e.y
        (graphics as Graphics2D).color = Color.black //设置画笔颜色
        graphics.stroke = BasicStroke(15F) //设置画笔大小
        graphics.drawLine(startX, startY, endX, endY) //画从上次到当前位置的直线
        //更新位置信息
        startX = endX
        startY = endY
    }

    //鼠标按下
    fun mousePressed(e: MouseEvent) { //重置startX，startY
        startX = e.x
        startY = e.y
    }

    @JvmStatic
    fun main(args: Array<String>) {
        //MnistDao.train()

        view = View //视图
        view.isVisible = true
        Controller() //控制器
    }
}