package bpMnist

import java.util.*
import kotlin.math.exp


class BP(var size: IntArray, eta: Double = 0.25, momentum: Double = 0.9) {
    private var layers //输入层、隐含层、输出层
            : Array<DoubleArray>
    private var deltas //每层误差
            : Array<DoubleArray>
    private var weights //权值
            : Array<Array<DoubleArray>>
    private var prevUptWeights //更新之前的权值信息
            : Array<Array<DoubleArray>>
    private var target //预测的输出内容
            : DoubleArray
    private var eta //学习率
            : Double
    private var momentum //动量参数
            : Double
    private var random //主要是对权值采取的是随机产生的方法
            : Random

    //初始化
    init {
        val len = size.size
        //初始化每层
        layers = Array(len) {
            DoubleArray(size[it] + 1)
        }
        //初始化预测输出
        target = DoubleArray(size[len - 1] + 1)
        //初始化隐藏层和输出层的误差
        deltas = Array(len - 1) {
            DoubleArray(size[it + 1] + 1)
        }
        //使每次产生的随机数都是第一次的分配，这是有参数和没参数的区别
        random = Random(100000)
        //初始化权值
        weights = Array(len - 1) { i ->
            Array(size[i] + 1) { DoubleArray(size[i + 1] + 1) }
        }
        randomizeWeights(weights)
        //初始化更新前的权值
        prevUptWeights = Array(len - 1) { i ->
            Array(size[i] + 1) { DoubleArray(size[i + 1] + 1) }
        }
        this.eta = eta //学习率
        this.momentum = momentum //动态量
    }

    //随机产生神经元之间的权值信息
    private fun randomizeWeights(matrix: Array<Array<DoubleArray>>) {
        var i = 0
        val len = matrix.size
        while (i != len) {
            var j = 0
            val len2: Int = matrix[i].size
            while (j != len2) {
                var k = 0
                val len3: Int = matrix[i][j].size
                while (k != len3) {
                    val real = random.nextDouble() //随机分配着产生0-1之间的值
                    matrix[i][j][k] = if (random.nextDouble() > 0.5) real else -real
                    k++
                }
                j++
            }
            i++
        }
    }

    //训练数据
    fun train(trainData: DoubleArray, target: DoubleArray) {
        loadValue(trainData, layers[0]) //加载输入的数据
        loadValue(target, this.target) //加载输出的结果数据
        forward() //向前计算神经元权值(先算输入到隐含层的，然后再算隐含到输出层的权值)
        calculateDelta() //计算误差逆传播值
        adjustWeight() //调整更新神经元的权值
    }

    //加载数据
    private fun loadValue(value: DoubleArray, layer: DoubleArray?) {
        require(value.size == layer!!.size - 1) { "Size Do Not Match." }
        System.arraycopy(value, 0, layer, 1, value.size) //调用系统复制数组的方法(存放输入的训练数据)
    }

    //向前计算(先算输入到隐含层的，然后再算隐含到输出层的权值)
    private fun forward() { //计算隐含层到输出层的权值
        for (i in 0 until layers.size - 1) {
            forward(layers[i], layers[i + 1], weights[i])
        }
    }

    //计算每一层的误差(因为在BP中，要达到使误差最小)(就是逆传播算法，书上有P101)
    private fun calculateDelta() {
        outputErr(deltas[deltas.size - 1], layers[layers.size - 1], target) //计算输出层的误差(因为要反过来算，所以先算输出层的)
        for (i in layers.size - 1 downTo 2) {
            hiddenErr(
                deltas[i - 2],
                layers[i - 1],
                deltas[i - 1],
                weights[i - 1]
            ) //计算隐含层的误差
        }
    }

    //更新每层中的神经元的权值信息
    private fun adjustWeight() {
        for (i in layers.size - 1 downTo 1) {
            adjustWeight(deltas[i - 1], layers[i - 1], weights[i - 1], prevUptWeights[i - 1])
        }
    }

    //向前计算各个神经元的权值(layer0：某层的数据,layer1：下一层的内容，weight：某层到下一层的神经元的权值)
    private fun forward(
        layer0: DoubleArray,
        layer1: DoubleArray,
        weight: Array<DoubleArray>
    ) {
        layer0[0] = 1.0 //给偏置神经元赋值为1（实际上添加了layer1层每个神经元的阙值）简直漂亮!!!
        var j = 1
        val len = layer1.size
        while (j != len) {
            var sum = 0.0 //保存权值
            var i = 0
            val len2 = layer0.size
            while (i != len2) {
                sum += weight[i][j] * layer0[i]
                ++i
            }
            layer1[j] = sigmoid(sum) //调用神经元的激活函数来得到结果(结果肯定是在0-1之间的)
            ++j
        }
    }

    //计算输出层的误差(delte:误差，output:输出，target：预测输出)
    private fun outputErr(
        delte: DoubleArray?,
        output: DoubleArray?,
        target: DoubleArray
    ) {
        for (idx in 1 until delte!!.size) {
            val o = output!![idx]
            delte[idx] = o * (1.0 - o) * (target[idx] - o)
        }
    }

    //计算隐含层的误差(delta:本层误差,layer：本层,delta1：下一层误差,weights：权值)
    private fun hiddenErr(
        delta: DoubleArray,
        layer: DoubleArray,
        delta1: DoubleArray,
        weights: Array<DoubleArray>
    ) {
        var j = 1
        val len = delta.size
        while (j != len) {
            val o = layer[j] //神经元权值
            var sum = 0.0
            var k = 1
            val len2 = delta1.size
            while (k != len2) {
                //由输出层来反向计算
                sum += weights[j][k] * delta1[k]
                ++k
            }
            delta[j] = o * (1.0 - o) * sum
            ++j
        }
    }

    //更新每层中的神经元的权值信息(这也就是不断的训练过程)
    private fun adjustWeight(
        delta: DoubleArray,
        layer: DoubleArray,
        weight: Array<DoubleArray>,
        prevWeight: Array<DoubleArray>
    ) {
        layer[0] = 1.0
        var i = 1
        val len = delta.size
        while (i != len) {
            var j = 0
            val len2 = layer.size
            while (j != len2) {
                //通过公式计算误差限=(动态量*之前的该神经元的阈值+学习率*误差*对应神经元的阈值)，来进行更新权值
                val newVal = momentum * prevWeight[j][i] + eta * delta[i] * layer[j]
                weight[j][i] += newVal //得到新的神经元之间的权值
                prevWeight[j][i] = newVal //保存这一次得到的权值，方便下一次进行更新
                ++j
            }
            ++i
        }
    }

    //我这里用的是sigmoid激活函数，当然也可以用阶跃函数，看自己选择吧
    private fun sigmoid(`val`: Double): Double {
        return 1.0 / (1.0 + exp(-`val`))
    }

    //测试神经网络
    fun test(inData: DoubleArray): Int {
        require(inData.size == layers[0].size - 1) { "Size Do Not Match." }
        System.arraycopy(inData, 0, layers[0], 1, inData.size)
        forward()
        return networkOutput
    }//获得最大权值下标

    //返回最后的输出层的结果
    private val networkOutput: Int
        get() {
            val len: Int = layers[layers.size - 1].size
            val temp = DoubleArray(len - 1)
            for (i in 1 until len) temp[i - 1] = layers[layers.size - 1][i]
            //获得最大权值下标
            var max = temp[0]
            var idx = -1
            for (i in temp.indices) {
                if (temp[i] >= max) {
                    max = temp[i]
                    idx = i
                }
            }
            return idx
        }

    //设置学习率
    fun setEta(eta: Double) {
        this.eta = eta
    }

    //设置动量参数
    fun setMomentum(momentum: Double) {
        this.momentum = momentum
    }


}