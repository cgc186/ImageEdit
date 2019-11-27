#include <opencv2/opencv.hpp>
#include <opencv2/dnn.hpp>
#include <iostream>

using namespace cv;
using namespace cv::dnn;
using namespace std;

const size_t width = 300;
const size_t height = 300;
const float meanVal = 127.5;
const float scaleFactor = 0.007843f;
const char* classNames[] = { "background",
"aeroplane", "bicycle", "bird", "boat",
"bottle", "bus", "car", "cat", "chair",
"cow", "diningtable", "dog", "horse",
"motorbike", "person", "pottedplant",
"sheep", "sofa", "train", "tvmonitor" };



void caffe(string modelFile, string model_text_file, string imgPath,string savePath)
{
	Net net = readNetFromCaffe(model_text_file, modelFile);
	Mat img = imread(imgPath);

	//预测
	Mat inputblob = blobFromImage(img, scaleFactor, Size(width, height), meanVal, false);
	net.setInput(inputblob, "data");
	Mat detection = net.forward("detection_out");

	//检测
	Mat detectionMat(detection.size[2], detection.size[3], CV_32F, detection.ptr<float>());
	float confidence_threshold = 0.25;
	for (int i = 0; i < detectionMat.rows; i++) {
		float confidence = detectionMat.at<float>(i, 2);
		if (confidence > confidence_threshold) {
			size_t objIndex = (size_t)(detectionMat.at<float>(i, 1));
			float tl_x = detectionMat.at<float>(i, 3) * img.cols;
			float tl_y = detectionMat.at<float>(i, 4) * img.rows;
			float br_x = detectionMat.at<float>(i, 5) * img.cols;
			float br_y = detectionMat.at<float>(i, 6) * img.rows;

			Rect object_box((int)tl_x, (int)tl_y, (int)(br_x - tl_x), (int)(br_y - tl_y));
			rectangle(img, object_box, Scalar(0, 0, 255), 2, 8, 0);
			putText(img, format("%s", classNames[objIndex]), Point(tl_x, tl_y), FONT_HERSHEY_SIMPLEX, 1.0, Scalar(255, 0, 0), 2);
		}
	}

	vector<int> param;
	param.push_back(CV_IMWRITE_PNG_COMPRESSION);
	param.push_back(0);

	imwrite(savePath, img, param);
}

int main() {
	string modelFile = "Z:/MobileNetSSD_deploy.caffemodel";
	string model_text_file = "Z:/MobileNetSSD_deploy.prototxt";
	string imagepath = "Z:/rgb.jpg";
	string savePath = "D:/out.png";
	caffe(modelFile, model_text_file, imagepath, savePath);
	return 0;
}