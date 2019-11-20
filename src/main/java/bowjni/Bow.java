package bowjni;

public class Bow {
    static {
        System.loadLibrary("data/Bow");
    }

    public native void train(
            int _clusters,
            String dataFolder,
            String trainFolder,
            String templateFolder,
            String testFolder,
            String resultFolder
    );

    public native String categoryImage(
            String trainPicPath,
            String dataFolder
    );

    public native void categoryBySvm(
            String dataFolder,
            String testFolder,
            String resultFolder,
            String templateFolder,
            int flag
    );

    public static void main(String[] args) {
        Bow b = new Bow();
        int clusters = 1000;

//        String dataFolder = "data/bowData/";
//        String trainFolder = "data/bowData/train_images/";
//        String templateFolder = "data/bowData/templates/";
//        String testFolder = "data/bowData/test_image";
//        String resultFolder = "data/bowData/result_image/";

        String dataFolder = "D:/project data/data/";
        String trainFolder = "D:/project data/data/train_images/";
        String templateFolder = "D:/project data/data/templates/";
        String testFolder = "D:/project data/data/test_image";
        String resultFolder = "D:/project data/data/result_image/";

        b.train(clusters, dataFolder, trainFolder, templateFolder, testFolder, resultFolder);
        String img = "D:/project data/data/test_image/0.jpg";
        String s = b.categoryImage(img, dataFolder);
        System.out.println(s);
        //将测试图片分类
        b.categoryBySvm(dataFolder, testFolder, resultFolder,templateFolder, 1);

    }
}
