package cnn

import cnn.core.CNN
import cnn.core.CNN.LayerBuilder
import cnn.core.Layer
import cnn.data.DataSet
import java.io.BufferedReader
import java.io.FileReader

object CnnDao {
    private const val MODEL_NAME = "data/mnist/model/model.cnn"

    private const val TRAIN_DATA = "data/mnist/train.format"

    private const val TEST_DATA = "data/mnist/test2.format"

    private const val TEST_PREDICT = "data/mnist/test.predict"

    fun initCnn(): CNN { // 构建网络层次结构
        val builder = LayerBuilder()
        // 输入层输出map大小为28×28
        builder.addLayer(Layer.buildInputLayer(Layer.Size(28, 28)))
        // 卷积层输出map大小为24×24,24=28+1-5
        builder.addLayer(Layer.buildConvLayer(6, Layer.Size(5, 5)))
        // 采样层输出map大小为12×12,12=24/2
        builder.addLayer(Layer.buildSampLayer(Layer.Size(2, 2)))
        // 卷积层输出map大小为8×8,8=12+1-5
        builder.addLayer(Layer.buildConvLayer(12, Layer.Size(5, 5)))
        // 采样层输出map大小为4×4,4=8/2
        builder.addLayer(Layer.buildSampLayer(Layer.Size(2, 2)))
        builder.addLayer(Layer.buildOutputLayer(10))
        return CNN(builder, 10)
    }

    fun train() {
        // 构建CNN
        val cnn = initCnn()
        // 加载训练数据
        val dataset = DataSet.load(TRAIN_DATA, ",", 784)
        // 开始训练模型
        cnn.train(dataset, 5)
        // 保存训练好的模型
        cnn.saveModel(MODEL_NAME)
        dataset.clear()
    }

    private fun test() { // 加载训练好的模型
        val cnn = CNN.loadModel(MODEL_NAME)
        // 加载测试数据
        val testSet = DataSet.load(TEST_DATA, ",", -1)
        // 预测结果
        cnn.predict(testSet, TEST_PREDICT)
        testSet.clear()
    }

    fun predict(): String? {
        test()
        val result = BufferedReader(FileReader(TEST_PREDICT))
        val num = result.readLine()
        println(num)
        return num
    }
}