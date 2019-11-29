package editjni;

public class EditImage {
    static{
        System.loadLibrary("data/dll/EditImage");
    }
    public native void surf(String path,String savePath);
    public native void cornerHairrs(String path,String savePath);

    public static void main(String[] args) {
        EditImage e = new EditImage();
        String path = "E:/备份/OneDrive - Dezhkeda/壁纸/新建文件夹 (7)/1.jpg";
        String savePath = "D:/11out.png";
        e.surf(path,savePath);
    }
}
