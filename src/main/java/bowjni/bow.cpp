// Bow.cpp : 定义 DLL 的导出函数。
//

#include "framework.h"
#include "Bow.h"

#include "jni.h"
#include "bowjni_Bow.h"

#include<iostream>
#include<map>
#include<opencv2/core/core.hpp>
#include<opencv2/highgui/highgui.hpp>
#include<string>
#include<opencv2/ml/ml.hpp>
#include<opencv2/features2d/features2d.hpp>
#include<opencv2/nonfree/features2d.hpp>
#include<opencv2/imgproc/imgproc.hpp>
#include<fstream>
//boost 库
#include<boost/filesystem.hpp>


using namespace cv;
using namespace std;
//定义一个boost库的命名空间
namespace fs = boost::filesystem;
using namespace fs;

// 这是导出变量的一个示例
BOW_API int nBow=0;

// 这是导出函数的一个示例。
BOW_API int fnBow(void)
{
    return 0;
}

// 这是已导出类的构造函数。
CBow::CBow()
{
    return;
}

std::string jstring2str(JNIEnv* env, jstring jstr) {
	char* rtn = NULL;
	jclass clsstring = env->FindClass("java/lang/String");
	jstring strencode = env->NewStringUTF("GB2312");
	jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
	jbyteArray barr = (jbyteArray)env->CallObjectMethod(jstr, mid, strencode);
	jsize alen = env->GetArrayLength(barr);
	jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
	if (alen > 0)
	{
		rtn = (char*)malloc(alen + 1);
		memcpy(rtn, ba, alen);
		rtn[alen] = 0;
	}
	env->ReleaseByteArrayElements(barr, ba, 0);
	std::string stemp(rtn);
	free(rtn);
	return stemp;
}

jstring charTojstring(JNIEnv* env, const char* pat) {
	//定义java String类 strClass
	jclass strClass = (env)->FindClass("Ljava/lang/String;");
	//获取String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
	jmethodID ctorID = (env)->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
	//建立byte数组
	jbyteArray bytes = (env)->NewByteArray(strlen(pat));
	//将char* 转换为byte数组
	(env)->SetByteArrayRegion(bytes, 0, strlen(pat), (jbyte*)pat);
	// 设置String, 保存语言类型,用于byte数组转换至String时的参数
	jstring encoding = (env)->NewStringUTF("GB2312");
	//将byte数组转换为java String,并输出
	return (jstring)(env)->NewObject(strClass, ctorID, bytes, encoding);
}

class bowTrain {
private:
	//存放所有训练图片的BOW
	map<string, Mat> allsamples_bow;
	//从类目名称到训练图集的映射，关键字可以重复出现
	multimap<string, Mat> train_set;
	// 训练得到的SVM
	CvSVM* stor_svms;
	//类目名称，也就是TRAIN_FOLDER设置的目录名
	vector<string> category_name;
	//类目数目
	int categories_size;
	//用SURF特征构造视觉词库的聚类数目
	int clusters;
	//存放训练图片词典
	Mat vocab;

	//特征检测器detectors与描述子提取器extractors   泛型句柄类Ptr
	Ptr<FeatureDetector> featureDecter;
	Ptr<DescriptorExtractor> descriptorExtractor;

	Ptr<BOWKMeansTrainer> bowtrainer;
	Ptr<BOWImgDescriptorExtractor> bowDescriptorExtractor;
	Ptr<FlannBasedMatcher> descriptorMacher;

	//构造训练集合
	void makeTrainSet();
	// 移除扩展名，用来讲模板组织成类目
	string removeExtention(string);

	string dataFolder;
	string trainFolder;
	string templateFolder;
	string testFolder;
	string resultFolder;

public:
	void train(int _clusters, string dF, string tF, string tempF, string testF, string rF);
	//初始化函数
	void init(int _clusters, string dF, string tF, string tempF, string testF, string rF);
	// 聚类得出词典
	void bulidVacab();
	//构造BOW
	void computeBowImage();
	//训练分类器
	void trainSvm();
};

// 移除扩展名，用来讲模板组织成类目
string bowTrain::removeExtention(string full_name) {
	//find_last_of找出字符最后一次出现的地方

	int last_index = full_name.find_last_of(".");
	string name = full_name.substr(0, last_index);
	return name;
}

void bowTrain::train(int _clusters, string dF, string tF, string tempF, string testF, string rF) {
	init(_clusters, dF, tF, tempF, testF, rF);

	//读取训练集
	makeTrainSet();
	//特征聚类
	bulidVacab();
	//构造BOW
	computeBowImage();
	//训练分类器
	trainSvm();
}


// 初始化函数
void bowTrain::init(int _clusters, string dF, string tF, string tempF, string testF, string rF) {
	cout << "Start initialization..." << endl;
	clusters = _clusters;

	dataFolder = dF;
	trainFolder = tF;
	templateFolder = tempF;
	testFolder = testF;
	resultFolder = rF;

	//初始化指针
	featureDecter = new SurfFeatureDetector();
	descriptorExtractor = new SurfDescriptorExtractor();

	bowtrainer = new BOWKMeansTrainer(clusters);
	descriptorMacher = new FlannBasedMatcher();
	bowDescriptorExtractor = new BOWImgDescriptorExtractor(descriptorExtractor, descriptorMacher);

	cout << "Initialization complete..." << endl;
}

//构造训练集合
void bowTrain::makeTrainSet() {
	cout << "Read trainingSet......" << endl;
	string categor;
	//递归迭代rescursive 直接定义两个迭代器：i为迭代起点（有参数），end_iter迭代终点
	int index = 0;
	for (recursive_directory_iterator i(trainFolder), end_iter; i != end_iter; i++) {
		index++;
		// level == 0即为目录，因为TRAIN__FOLDER中设置如此
		if (i.level() == 0) {
			// 将类目名称设置为目录的名称
			categor = (i->path()).filename().string();
			category_name.push_back(categor);
		}
		else {
			// 读取文件夹下的文件。level 1表示这是一副训练图，通过multimap容器来建立由类目名称到训练图的一对多的映射
			string filename = string(trainFolder) + categor + string("/") + (i->path()).filename().string();
			Mat temp = imread(filename, CV_LOAD_IMAGE_GRAYSCALE);
			pair<string, Mat> p(categor, temp);
			//得到训练集
			train_set.insert(p);
			//cout << p.first << endl;
		}
	}
	categories_size = category_name.size();
	cout << categories_size << "types of objects found..." << endl;
}


// 训练图片feature聚类，得出词典
void bowTrain::bulidVacab() {
	FileStorage vacab_fs(dataFolder + "svm/" + "vocab.xml", FileStorage::READ);
	//如果之前已经生成好，就不需要重新聚类生成词典
	if (vacab_fs.isOpened()) {
		cout << "Pictures have been clustered and dictionaries already exist.." << endl;
		vacab_fs.release();
	}
	else {
		Mat vocab_descriptors;
		vector<KeyPoint>kp;
		// 对于每一幅模板，提取SURF算子，存入到vocab_descriptors中
		multimap<string, Mat> ::iterator i = train_set.begin();
		for (; i != train_set.end(); i++) {
			Mat templ = (*i).second;
			Mat descrip;
			featureDecter->detect(templ, kp);
			descriptorExtractor->compute(templ, kp, descrip);

			//push_back(Mat);在原来的Mat的最后一行后再加几行,元素为Mat时， 其类型和列的数目 必须和矩阵容器是相同的
			vocab_descriptors.push_back(descrip);
		}
		cout << "Training picture start clustering..." << endl;
		//将每一副图的Surf特征利用add函数加入到bowTraining中去,就可以进行聚类训练了
		bowtrainer->add(vocab_descriptors);
		// 对SURF描述子进行聚类
		vocab = bowtrainer->cluster();
		cout << "Clustering finished,a dictionary is obtained..." << endl;
		//以文件格式保存词典
		FileStorage file_stor(dataFolder + "svm/" + "vocab.xml", FileStorage::WRITE);
		file_stor << "vocabulary" << vocab;
		file_stor.release();
	}
}


//构造bag of words
void bowTrain::computeBowImage() {
	cout << "structure bag of words..." << endl;
	FileStorage va_fs(dataFolder + "svm/" + "vocab.xml", FileStorage::READ);
	//如果词典存在则直接读取
	if (va_fs.isOpened()) {
		Mat temp_vacab;
		va_fs["vocabulary"] >> temp_vacab;
		bowDescriptorExtractor->setVocabulary(temp_vacab);
		va_fs.release();
	}
	else {
		//对每张图片的特征点，统计这张图片各个类别出现的频率，作为这张图片的bag of words
		bowDescriptorExtractor->setVocabulary(vocab);
	}

	//如果bow.txt已经存在说明之前已经训练过了，下面就不用重新构造BOW
	string bow_path = dataFolder + "svm/" + string("bow.txt");
	std::ifstream read_file(bow_path);

	//如BOW已经存在，则不需要构造
	if (read_file.is_open()) {
		cout << "BOW Ready..." << endl;
	}
	else {
		// 对于每一幅模板，提取SURF算子，存入到vocab_descriptors中
		multimap<string, Mat> ::iterator i = train_set.begin();

		for (; i != train_set.end(); i++) {
			vector<KeyPoint>kp;
			string cate_nam = (*i).first;
			Mat tem_image = (*i).second;
			Mat imageDescriptor;
			featureDecter->detect(tem_image, kp);

			bowDescriptorExtractor->compute(tem_image, kp, imageDescriptor);
			//push_back(Mat);在原来的Mat的最后一行后再加几行,元素为Mat时， 其类型和列的数目 必须和矩阵容器是相同的
			allsamples_bow[cate_nam].push_back(imageDescriptor);
		}
		//简单输出一个文本，为后面判断做准备
		std::ofstream ous(bow_path);
		ous << "flag";
		cout << "bag of words construction completed..." << endl;
	}
}

//训练分类器

void bowTrain::trainSvm() {
	int flag = 0;
	for (int k = 0; k < categories_size; k++) {
		string svm_file_path = dataFolder + "svm/type/" + category_name[k] + string("SVM.xml");
		FileStorage svm_fil(svm_file_path, FileStorage::READ);
		//判断训练结果是否存在
		if (svm_fil.isOpened())
		{
			svm_fil.release();
			continue;
		}
		else
		{
			flag = -1;
			break;
		}
	}

	//如果训练结果已经存在则不需要重新训练
	if (flag != -1) {
		cout << "The classifier has been trained..." << endl;
	}
	else {
		stor_svms = new CvSVM[categories_size];
		//设置训练参数
		SVMParams svmParams;
		svmParams.svm_type = CvSVM::C_SVC;
		svmParams.kernel_type = CvSVM::LINEAR;
		svmParams.term_crit = cvTermCriteria(CV_TERMCRIT_ITER, 100, 1e-6);

		cout << "Training classifier..." << endl;
		for (int i = 0; i < categories_size; i++) {
			Mat tem_Samples(0, allsamples_bow.at(category_name[i]).cols, allsamples_bow.at(category_name[i]).type());
			Mat responses(0, 1, CV_32SC1);
			tem_Samples.push_back(allsamples_bow.at(category_name[i]));
			Mat posResponses(allsamples_bow.at(category_name[i]).rows, 1, CV_32SC1, Scalar::all(1));
			responses.push_back(posResponses);

			for (auto itr = allsamples_bow.begin(); itr != allsamples_bow.end(); ++itr) {
				if (itr->first == category_name[i]) {
					continue;
				}
				tem_Samples.push_back(itr->second);
				Mat response(itr->second.rows, 1, CV_32SC1, Scalar::all(-1));
				responses.push_back(response);
			}

			stor_svms[i].train(tem_Samples, responses, Mat(), Mat(), svmParams);
			//存储svm
			string svm_filename = dataFolder + "svm/type/" + category_name[i] + string("SVM.xml");
			stor_svms[i].save(svm_filename.c_str());
		}
		cout << "Classifier training completed..." << endl;
	}
}

String categoryImage(string trainPicPath, string dataFolder) {

	vector<string> category_name;
	int categoryNameSize = 0;

	for (recursive_directory_iterator i(dataFolder + "svm/type/"), end_iter; i != end_iter; i++) {
		string categor = (i->path()).filename().string();

		int last_index = categor.find_last_of(".");
		string name = categor.substr(0, last_index - 3);

		category_name.push_back(name);
	}
	categoryNameSize = category_name.size();

	Mat gray_pic;
	string prediction_category;
	float curConfidence;

	//读取图片

	//cout << "Input picture: " << trainPicPath << endl;

	Mat input_pic = imread(trainPicPath);
	cvtColor(input_pic, gray_pic, CV_BGR2GRAY);

	// 提取BOW描述子
	vector<KeyPoint>kp;
	Mat test;

	Ptr<FeatureDetector> featureDecter = new SurfFeatureDetector();

	Ptr<DescriptorExtractor> descriptorExtractor = new SurfDescriptorExtractor();

	Ptr<FlannBasedMatcher> descriptorMacher = new FlannBasedMatcher();
	Ptr<BOWImgDescriptorExtractor> bowDescriptorExtractor = new BOWImgDescriptorExtractor(descriptorExtractor, descriptorMacher);

	FileStorage va_fs(dataFolder + "svm/" + "vocab.xml", FileStorage::READ);
	//如果词典存在则直接读取
	if (va_fs.isOpened()) {
		Mat temp_vacab;
		va_fs["vocabulary"] >> temp_vacab;
		bowDescriptorExtractor->setVocabulary(temp_vacab);
		va_fs.release();
	}

	featureDecter->detect(gray_pic, kp);
	bowDescriptorExtractor->compute(gray_pic, kp, test);

	int sign = 0;
	float best_score = -2.0f;

	for (int i = 0; i < categoryNameSize; i++) {
		string cate_na = category_name[i];

		string f_path = dataFolder + "svm/type/" + cate_na + string("SVM.xml");
		FileStorage svm_fs(f_path, FileStorage::READ);
		//读取SVM.xml+99
		if (svm_fs.isOpened()) {
			svm_fs.release();
			CvSVM st_svm;
			st_svm.load(f_path.c_str());

			if (sign == 0) {
				float score_Value = st_svm.predict(test, true);
				float class_Value = st_svm.predict(test, false);
				sign = (score_Value < 0.0f) == (class_Value < 0.0f) ? 1 : -1;
			}
			curConfidence = sign * st_svm.predict(test, true);
		}
		if (curConfidence > best_score) {
			best_score = curConfidence;
			prediction_category = cate_na;
		}
	}
	return prediction_category;
}

void categoryBySvm(
	string dataFolder,
	string testFolder
	, string resultFolder,
	string templateFolder,
	int flag) {
	cout << "Start of object classification..." << endl;

	directory_iterator begin_train(testFolder);
	directory_iterator end_train;

	map<string, Mat> result_objects;
	directory_iterator begin_iter(templateFolder);
	directory_iterator end_iter;
	//获取该目录下的所有文件名
	for (; begin_iter != end_iter; ++begin_iter) {
		string imageName = begin_iter->path().filename().string();

		string filename = templateFolder + imageName;

		int last_index = imageName.find_last_of(".");
		string name = imageName.substr(0, last_index);

		//读入模板图片
		Mat image = imread(filename);
		//Mat templ_image;
		//存储原图模板
		result_objects[name] = image;
	}


	for (; begin_train != end_train; ++begin_train) {
		//获取该目录下的图片名
		string trainPicName = (begin_train->path()).filename().string();
		string trainPicPath = testFolder + string("/") + (begin_train->path()).filename().string();

		//cout << trainPicPath << endl;

		string prediction_category = categoryImage(trainPicPath, dataFolder);

		Mat input_pic = imread(trainPicPath);

		//将图片写入相应的文件夹下
		directory_iterator begin_iterater(resultFolder);
		directory_iterator end_iterator;
		//获取该目录下的文件名
		for (; begin_iterater != end_iterator; ++begin_iterater) {
			if (begin_iterater->path().filename().string() == prediction_category) {
				string filename = resultFolder + prediction_category + string("/") + trainPicName;
				imwrite(filename, input_pic);
			}
		}
		cout << "Input picture: " << trainPicPath << endl;
		cout << "This picture belongs to: " << prediction_category << endl;
		//显示输出

		if (flag == 1) {
			imshow("Input picture: ", input_pic);

			namedWindow("Dectect Object");

			imshow("Dectect Object", result_objects[prediction_category]);
			waitKey(0);
		}
	}
}

JNIEXPORT void JNICALL Java_bowjni_Bow_train
(JNIEnv* env, jobject,
	jint _clusters,
	jstring jdataFolder,
	jstring jtrainFolder,
	jstring jtemplateFolder,
	jstring jtestFolder,
	jstring jresultFolder) {

	string dataFolder = jstring2str(env, jdataFolder);
	string trainFolder = jstring2str(env, jtrainFolder);
	string templateFolder = jstring2str(env, jtemplateFolder);
	string testFolder = jstring2str(env, jtestFolder);
	string resultFolder = jstring2str(env, jresultFolder);
	bowTrain c;
	c.train(_clusters, dataFolder, trainFolder, templateFolder, testFolder, resultFolder);
}

JNIEXPORT jstring JNICALL Java_bowjni_Bow_categoryImage
(JNIEnv* env, jobject, jstring jtrainPicPath, jstring jdataFolder) {

	string trainPicPath = jstring2str(env, jtrainPicPath);
	string dataFolder = jstring2str(env, jdataFolder);
	string prediction_category = categoryImage(trainPicPath, dataFolder);
	jstring predictionCategory = env->NewStringUTF(prediction_category.c_str());
	return predictionCategory;
}

JNIEXPORT void JNICALL Java_bowjni_Bow_categoryBySvm
(JNIEnv* env,
	jobject,
	jstring jdataFolder,
	jstring jtestFolder,
	jstring jresultFolder,
	jstring jtemplateFolder,
	jint flag) {
	string dataFolder = jstring2str(env, jdataFolder);
	string testFolder = jstring2str(env, jtestFolder);
	string templateFolder = jstring2str(env, jtemplateFolder);
	string resultFolder = jstring2str(env, jresultFolder);
	categoryBySvm(
		dataFolder,
		testFolder,
		resultFolder,
		templateFolder,
		(int)flag);
}