package cnn;

import cnn.core.CNN;
import cnn.core.Layer;
import cnn.utils.ConcurentRunner;
import cnn.core.CNN.LayerBuilder;
import cnn.core.Layer.Size;
import cnn.data.DataSet;


public class Main {
    private static final String MODEL_NAME = "data/mnist/model/model.cnn";

    private static final String TRAIN_DATA = "data/mnist/train.format";

    private static final String TEST_DATA = "data/mnist/test.format";

    private static final String TEST_PREDICT = "data/mnist/test.predict";

    public static void main(String[] args) {
        System.err.println("训练阶段：");
        runTrain();
        System.err.println("测试阶段：");
        runTest();
        ConcurentRunner.stop();
    }

    private static void runTrain() {
        // 构建网络层次结构
        CNN.LayerBuilder builder = new LayerBuilder();
        // 输入层输出map大小为28×28
        builder.addLayer(Layer.buildInputLayer(new Size(28, 28)));
        // 卷积层输出map大小为24×24,24=28+1-5
        builder.addLayer(Layer.buildConvLayer(6, new Size(5, 5)));
        // 采样层输出map大小为12×12,12=24/2
        builder.addLayer(Layer.buildSampLayer(new Size(2, 2)));
        // 卷积层输出map大小为8×8,8=12+1-5
        builder.addLayer(Layer.buildConvLayer(12, new Size(5, 5)));
        // 采样层输出map大小为4×4,4=8/2
        builder.addLayer(Layer.buildSampLayer(new Size(2, 2)));
        builder.addLayer(Layer.buildOutputLayer(10));
        CNN cnn = new CNN(builder, 10);
        // 加载训练数据
        DataSet dataset = DataSet.load(TRAIN_DATA, ",", 784);
        // 开始训练模型
        cnn.train(dataset, 5);
        // 保存训练好的模型
        cnn.saveModel(MODEL_NAME);
        dataset.clear();
    }

    private static void runTest() {
        // 加载训练好的模型
        CNN cnn = CNN.loadModel(MODEL_NAME);
        // 加载测试数据
        DataSet testSet = DataSet.load(TEST_DATA, ",", -1);
        // 预测结果
        cnn.predict(testSet, TEST_PREDICT);
        testSet.clear();
    }
}
