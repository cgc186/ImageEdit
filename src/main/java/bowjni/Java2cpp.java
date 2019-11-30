package bowjni;

public class Java2cpp {
    static {
        System.loadLibrary("data/javaToCpp");
    }

    public native void train(int _clusters, String dF, String tF, String tempF, String testF, String rF);

    public native void categoryImage(
            String trainPicName,
            String trainPicPath,
            String dataFolder
    );

    public native void categoryBySvm(String dataFolder, String testFolder);

    public static void main(String[] args) {
        int clusters = 1000;

        String dataFolder = "data/bowData/";
        String trainFolder = "data/bowData/train_images/";
        String templateFolder = "data/bowData/templates/";
        String testFolder = "data/bowData/test_image";
        String resultFolder = "data/bowData/result_image/";

        Java2cpp tt = new Java2cpp();

        tt.train(clusters, dataFolder, trainFolder, templateFolder, testFolder, resultFolder);

        //将测试图片分类
        tt.categoryBySvm(dataFolder, testFolder);
    }
}
