package caffejni;

public class Caffe {
    static{
        System.loadLibrary("data/dll/Caffe");
    }
    public native void caffe(String modelFile,
                             String modelTextFile,
                             String path,
                             String savePath);
    public native String getType(String modelTxt,String modelBin,String synSetWords,String imageFile);
//            "background",
//            "aeroplane", "bicycle", "bird", "boat",
//            "bottle", "bus", "car", "cat", "chair",
//            "cow", "diningtable", "dog", "horse",
//            "motorbike", "person", "pottedplant",
//            "sheep", "sofa", "train", "tvmonitor"

    public native void pca(String path,
                           String savePath);

    public static void main(String[] args) {
        Caffe c = new Caffe();
//        String modelFile = "Z:/MobileNetSSD_deploy.caffemodel";
//        String modelTextFile = "Z:/MobileNetSSD_deploy.prototxt";
//        String imagePath = "Z:/rgb.jpg";
//        String savePath = "D:/out.png";
//        c.caffe(modelFile, modelTextFile, imagePath, savePath);

        String modelTxt = "Z:/bvlc_googlenet.prototxt";
        String modelBin = "Z:/bvlc_googlenet.caffemodel";
        String synSetWords = "Z:/synset_words.txt";
        String imageFile = "Z:/1.png";
        String s = c.getType(modelTxt, modelBin, synSetWords,imageFile);
        System.out.println(s);
    }
}
