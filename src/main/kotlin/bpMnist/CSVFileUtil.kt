package bpMnist

import java.io.BufferedReader
import java.io.FileReader
import java.util.*


class CSVFileUtil(fileName: String?) {
    private var br: BufferedReader? = null //文件
    private val list: MutableList<String> = ArrayList() //String类型数据
    //获取列数
    val colNum  //列数
        get() = list[0].split(",").toTypedArray().size

    private lateinit var data //double类型数据
            : Array<DoubleArray>

    //获取行数
    val rowNum: Int
        get() = list.size

    //获取某行（String[]）
    fun getRowString(row: Int): List<String> {
        return list[row].split(",")
    }

    //获取某行（double[]）
    fun getRowList(index: Int): DoubleArray {
        return data[index]
    }

    //获取某一单元格
    fun getDouble(row: Int, col: Int): Double {
        return data[row][col]
    }

    //设置某一单元格
    fun setDouble(row: Int, col: Int, value: Int) {
        data[row][col] = value.toDouble()
    }

    //设置数据
    fun setData(data: Array<DoubleArray>) {
        this.data = data
    }

    //构造函数
    init {
        br = BufferedReader(FileReader(fileName)) //打开CSV文件

        var stemp: String?
        while (br!!.readLine().also { stemp = it } != null) {
            list.add(stemp!!) //存到list里
        }
        br!!.close() //关闭文件

    }
}