package bowjni;

public class Java2cpp {
    static{
        System.loadLibrary("javaToCpp");
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

        String dataFolder = "D:/project data/data/";
        String trainFolder = "D:/project data/data/train_images/";
        String templateFolder = "D:/project data/data/templates/";
        String testFolder = "D:/project data/data/test_image";
        String resultFolder = "D:/project data/data/result_image/";

        Java2cpp tt = new Java2cpp();

        tt.train(clusters, dataFolder, trainFolder, templateFolder, testFolder, resultFolder);

        //将测试图片分类
        //tt.categoryBySvm(dataFolder, testFolder);
    }
}
