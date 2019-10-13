package bow.util

import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
/**
 * 序列化辅助类
 */
class SerializationUtils {
    /* dump object to output file */
    @Throws(Exception::class)
    fun dumpObject(output: String, obj: Any) {
        val oos = ObjectOutputStream(
            FileOutputStream(
                output
            )
        )
        oos.writeObject(obj)
        oos.close()
    }

    /* load object from input file */
    @Throws(Exception::class)
    fun loadObject(input: String): Any {
        val ois = ObjectInputStream(
            FileInputStream(input)
        )
        val cls = ois.readObject()
        ois.close()
        return cls
    }
}