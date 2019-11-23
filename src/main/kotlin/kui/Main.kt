package kui

import jui.Main
import kui.menu.Menu
import javax.swing.WindowConstants

fun main() {

    val t = ImageJFrame

    t.jf.setSize(800, 800)

    t.jf.setLocationRelativeTo(null)
    t.jf.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    t.resizeListener()
    t.jf.jMenuBar = Menu.menuBar
    t.jf.contentPane = MyJSplitPane.getJSplitPane()
    t.jf.isVisible = true
}