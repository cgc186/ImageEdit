package edge;

import second.edgeDetection.ImageUtil;

import java.awt.image.BufferedImage;

public class MyImage {

    public int h; //高
    public int w; //宽
    public int[] data; //像素
    public boolean gray; //是否为灰度图像

    public MyImage(BufferedImage img) {

        this.h = img.getHeight();
        this.w = img.getWidth();

        this.data = img.getRGB(0, 0, w, h, null, 0, w);
        this.gray = false;
        toGray();//灰度化


    }

    public MyImage(BufferedImage img, boolean gray) {

        this.h = img.getHeight();
        this.w = img.getWidth();
        this.data = img.getRGB(0, 0, w, h, null, 0, w);
        this.gray = gray;

    }


    public MyImage(int[] data, int h, int w) {
        this.data = (data == null) ? new int[w * h] : data;
        this.h = h;
        this.w = w;
        this.gray = false;
    }

    public MyImage(int h, int w) {
        this(null, h, w);
    }


    public BufferedImage toImage() {
        BufferedImage image = new BufferedImage(this.w, this.h, BufferedImage.TYPE_INT_ARGB);
        int[] d = new int[w * h];
        for (int i = 0; i < this.h; i++) {
            for (int j = 0; j < this.w; j++) {
                if (this.gray) {
                    d[j + i * this.w] = (255 << 24) | (data[j + i * this.w] << 16) | (data[j + i * this.w] << 8) | (data[j + i * this.w]);
                } else {
                    d[j + i * this.w] = data[j + i * this.w];
                }
            }
        }
        image.setRGB(0, 0, w, h, d, 0, w);
        return image;
    }

    public void toGray() {
        if (!gray) {
            this.gray = true;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int c = this.data[x + y * w];
                    int R = (c >> 16) & 0xFF;
                    int G = (c >> 8) & 0xFF;
                    int B = (c >> 0) & 0xFF;
                    this.data[x + y * w] = (int) (0.3f * R + 0.59f * G + 0.11f * B); //to gray
                }
            }
        }
    }

}
