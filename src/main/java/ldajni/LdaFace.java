package ldajni;

public class LdaFace {
    static{
        System.loadLibrary("data/LdaFace");
    }
    public native void train(String imgTrainList);
    public native void test(String testFile, String templates);
    public native int predict(String imagePath);

    public static void main(String[] args) {
        LdaFace lf = new LdaFace();
        String imgTrainList = "data/ldaData/imgTrainList.txt";
        lf.train(imgTrainList);

        String testFile = "data/ldaData/test.txt";
        String templates = "data/ldaData/templates.txt";
        lf.test(testFile, templates);
    }
}
