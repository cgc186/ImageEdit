package lda

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.videoio.VideoCapture

import javax.swing.*
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.Objects

internal class CameraBasic338 private constructor() {

    private var frame: JFrame? = null

    init {
        initialize()
    }

    private fun initialize() {
        flag = 0
        frame = JFrame()
        frame!!.setBounds(100, 100, 800, 600)
        frame!!.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE//WindowConstants.EXIT_ON_CLOSE
        frame!!.contentPane.layout = null

        val btnNewButton = JButton("\u62CD\u7167")
        btnNewButton.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(arg0: MouseEvent?) {
                flag = 1//静态变量设置为1，从而按下按钮时会停止摄像头的调用
                frame!!.dispose()
            }
        })
        btnNewButton.setBounds(660, 13, 113, 27)
        frame!!.contentPane.add(btnNewButton)

        label = JLabel("")
        label!!.setBounds(0, 0, 700, 500)
        frame!!.contentPane.add(label)
    }

    companion object {

        init {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        }

        private var label: JLabel? = null
        private var flag = 0//类静态变量，用于控制按下按钮后 停止摄像头的读取

        fun photo() {
            EventQueue.invokeLater {
                try {
                    val window = CameraBasic338()
                    window.frame!!.isVisible = true

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            val camera = VideoCapture()//创建Opencv中的视频捕捉对象
            camera.open(0)//open函数中的0代表当前计算机中索引为0的摄像头，如果你的计算机有多个摄像头，那么一次1,2,3……
            if (!camera.isOpened) {//isOpened函数用来判断摄像头调用是否成功
                println("Camera Error")//如果摄像头调用失败，输出错误信息
            } else {
                val frame = Mat()//创建一个输出帧
                while (flag == 0) {
                    camera.read(frame)//read方法读取摄像头的当前帧
                    label!!.icon =
                        ImageIcon(Objects.requireNonNull(mat2BufferedImage.matToBufferedImage(frame)))//转换图像格式并输出
                    try {
                        Thread.sleep(100)//线程暂停100ms
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                }
                camera.release()
            }
        }
    }
}
