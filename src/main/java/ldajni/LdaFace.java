package ldajni;

public class LdaFace {
    static{
        System.loadLibrary("data/dll/LdaFace");
    }
    public native void train(String imgTrainList,String faceModelPath);
    public native void test(String testFile, String templates,String faceModelPath);
    public native int predict(String imagePath,String faceModelPath);

    public static void main(String[] args) {
        LdaFace lf = new LdaFace();
        String imgTrainList = "data/ldaData/imgTrainList.txt";
        String faceModelPath = "data/ldaData/";
        lf.train(imgTrainList,faceModelPath);

        String testFile = "data/ldaData/test.txt";
        String templates = "data/ldaData/templates.txt";

        lf.test(testFile,templates,faceModelPath);

//        String s = "E:/coding/c++/lda/FaceDB_orl/001/01.png";
//        int predict = lf.predict(s, faceModelPath);
//        System.out.println(predict);
    }
}
