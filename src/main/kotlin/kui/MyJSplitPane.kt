package kui

import kui.ImageJFrame.jf
import javax.swing.JLabel
import javax.swing.JSplitPane

object MyJSplitPane {
    fun getJSplitPane(): JSplitPane {
        ImageJFrame.jSplitPane = JSplitPane()
        ImageJFrame.jSplitPane!!.isOneTouchExpandable = true //让分割线显示出箭头
        ImageJFrame.jSplitPane!!.isContinuousLayout = true //操作箭头，重绘图形
        //jSplitPane.setPreferredSize(new Dimension (100,200));
        ImageJFrame.jSplitPane!!.orientation = JSplitPane.HORIZONTAL_SPLIT //设置分割线方向
        ImageJFrame.myPanel1.setSize(jf.width/2, jf.height)
        ImageJFrame.imageJLabel = JLabel()
        ImageJFrame.myPanel1.add(ImageJFrame.imageJLabel)
        ImageJFrame.myPanel2.setSize(jf.width/2, jf.height)
        ImageJFrame.editImageJLabel = JLabel()
        ImageJFrame.myPanel2.add(ImageJFrame.editImageJLabel)
        ImageJFrame.jSplitPane!!.leftComponent = ImageJFrame.myPanel1 //布局中添加组件 ，面板1
        ImageJFrame.jSplitPane!!.rightComponent = ImageJFrame.myPanel2 //添加面板2
        ImageJFrame.jSplitPane!!.dividerSize = 1 //设置分割线的宽度
        //jSplitPane.setDividerLocation(100);//设置分割线位于中央
        ImageJFrame.jSplitPane!!.dividerLocation = jf.width/2 //设定分割线的距离左边的位置
        return ImageJFrame.jSplitPane as JSplitPane
    }
}