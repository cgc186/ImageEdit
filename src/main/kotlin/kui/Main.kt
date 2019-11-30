package kui

import kui.menu.Menu
import java.awt.Container
import javax.swing.WindowConstants

fun main() {

    val jFrame = ImageJFrame

    jFrame.jf.setSize(800, 800)

    jFrame.jf.setLocationRelativeTo(null)
    jFrame.jf.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    jFrame.resizeListener()
    jFrame.jf.jMenuBar = Menu.menuBar
    jFrame.jf.contentPane = MyJSplitPane.getJSplitPane() as Container?
    jFrame.jf.isVisible = true
}