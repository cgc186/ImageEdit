package bpMnist

object MnistDao {
    fun train(){
        mnistModel.train()
    }

    fun test(arr:DoubleArray): Int {
        return mnistModel.test(arr)
    }
}