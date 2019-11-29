package editjni;


//import org.bytedeco.javacpp.opencv_core.*;
//
//
//import static org.bytedeco.javacpp.opencv_core.CV_PCA_DATA_AS_COL;
//import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
//import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
//import static org.bytedeco.javacpp.opencv_imgproc.COLOR_RGB2GRAY;
//import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;

public class Pca {
//    public void pca(String imgPath, String savePath) {
//        Mat img = imread(imgPath);
//        cvtColor(img, img, COLOR_RGB2GRAY);
//        PCA pca = new PCA(img, new Mat(), CV_PCA_DATA_AS_COL, 120);
//        Mat dst = pca.project(img);
//        Mat src = pca.backProject(dst);
//        imwrite(savePath, src);
//    }

    public static void main(String[] args) {
        Pca p = new Pca();
        //p.pca("Z:/11.png","G:/11.png");
    }
}
