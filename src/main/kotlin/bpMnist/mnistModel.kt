package bpMnist

import javax.swing.JOptionPane


object mnistModel {
    val dataPath = "E:/mnist_train.csv"
    val tabPath = "E:/mnist_train_labels.csv"

    //分别代表784个输入层，一层隐含层，其中是784个神经元，10个输出结果
    private val bp = BP(intArrayOf(784, 35, 10))
    //数据和标签
    private var util = CSVFileUtil(tabPath)
    private var util2 = CSVFileUtil(dataPath)


    //训练BP神经网络
    @Throws(Exception::class)
    fun train() {
        object : Thread() {
            override fun run() { //获取文件路径并处理
                try { //训练神经网络
                    val resulthang: Int = util.rowNum //得到训练结果行数
                    ProcessingData(util2) //处理数据
                    for (i in 0..0) { //训练迭代次数
                        for (index in 0 until resulthang) {
                            val trainResult = DoubleArray(10) //因为结果有0-9这10种情况
                            val getResult: List<String> = util.getRowString(index) //得到一个数据的标签
                            trainResult[getResult[0].toInt()] = 1.0 //表示当前的这个训练集的结果是对应的下标的值
                            val binary: DoubleArray = util2.getRowList(index) //转成二进制进行处理(784位)
                            bp.train(binary, trainResult) //训练数据
                            //View.setProgressBarValue(index / (inputhang / 100) + 50) // 设置进度条数值
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                println("训练完成")
                JOptionPane.showMessageDialog(null, "训练完成", "人脸检测", JOptionPane.INFORMATION_MESSAGE)
                //View.setProgressBarString("训练完成") // 设置提示信息
            }
        }.start() //  启动进度条线程
    }

    //测试BP神经网络
    fun test(rgbArray: DoubleArray): Int {

        //JOptionPane.showMessageDialog(null, "识别结果是：$result", "结果", JOptionPane.INFORMATION_MESSAGE)

        return bp.test(rgbArray)
    }

    //
    //处理数据
    fun ProcessingData(unit: CSVFileUtil) {
        val RowNum: Int = unit.rowNum //获取行数
        val ColNum: Int = unit.colNum //获取列数
        unit.setData(Array(RowNum) { DoubleArray(ColNum) }) //new空间
        for (i in 0 until RowNum) {
            val valueString = unit.getRowString(i) //获取一行数据
            for (j in 0 until ColNum) { //每一个数据都是一个灰度值，根据数据是否大于255的一半写入data数据
                unit.setDouble(i, j, if (valueString[j].toInt() >= 255 / 2) 1 else 0)
            }
            //View.setProgressBarValue(i / (RowNum / 100) / 10 * 5) // 设置进度条数值
        }
    }

//    //打开文件
//    fun openFile(style: String) {
//        val imageFile = JFileChooser() //创建文件选择对话框
//        imageFile.dialogTitle = "请选择需要打开的csv文件..."
//        val filter = FileNameExtensionFilter("csv文件(*.csv)", "csv")
//        imageFile.fileFilter = filter
//        val i = imageFile.showOpenDialog(view.getContentPane()) //显示文件选择对话框
//        if (i == JFileChooser.APPROVE_OPTION) { //判断用户单机的是否为“打开”按钮
//            val selectedFile = imageFile.selectedFile //获得选中的文件对象
//            if (style === "数据文件") {
//                View.setPath("数据文件", selectedFile.path) //显示文件路径
//            } else {
//                View.setPath("标签文件", selectedFile.path) //显示文件路径
//            }
//        }
//    }

    //将接收到的数据处理并返回
    fun handling(str: String): String {
        var str = str
        val strs = str.split(",").toTypedArray()
        val grays = DoubleArray(784)
        for (i in 0..783) {
            grays[i] = strs[i].toInt().toDouble()
        }
        val result = bp.test(grays) //测试的结果
        str = result.toString()
        str += "\r"
        return str
    }


}