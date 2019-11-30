package cluster;

public class test {

    public static void main(String[] args) {
        ImageCluster ic = new ImageCluster();
        ic.kmeans("E:/22.jpg", "E:/test.jpg", 3, 10, 1);
    }
}