package fim.uni_passau.de.countyourhits.util;


import android.os.Environment;
import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import fim.uni_passau.de.countyourhits.model.DetectedCircle;

import static org.opencv.imgproc.Imgproc.CV_HOUGH_GRADIENT;

public class ColorBlobDetector {
    public static int OUTER_CIRCLE_MIN_RADIUS=40;
    public static int INNER_CIRCLE_MIN_RADIUS=5;
    // Lower and Upper bounds for range checking in HSV color space
    private Scalar mLowerBound = new Scalar(0);
    private Scalar mUpperBound = new Scalar(0);
    // Minimum contour area in percent for contours filtering
    private static double mMinContourArea = 0.1;
    // Color radius for range checking in HSV color space
    private Scalar mColorRadius = new Scalar(25,50,50,0);
    private Mat mSpectrum = new Mat();
    private List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();

    // Cache
    Mat mPyrDownMat = new Mat();
    Mat mHsvMat = new Mat();
    Mat mMask = new Mat();
    Mat mDilatedMask = new Mat();
    Mat mHierarchy = new Mat();

    public void setColorRadius(Scalar radius) {
        mColorRadius = radius;
    }

    public void setHsvColor(Scalar hsvColor) {
        double minH = (hsvColor.val[0] >= mColorRadius.val[0]) ? hsvColor.val[0]-mColorRadius.val[0] : 0;
        double maxH = (hsvColor.val[0]+mColorRadius.val[0] <= 255) ? hsvColor.val[0]+mColorRadius.val[0] : 255;

        mLowerBound.val[0] = minH;
        mUpperBound.val[0] = maxH;

        mLowerBound.val[1] = hsvColor.val[1] - mColorRadius.val[1];
        mUpperBound.val[1] = hsvColor.val[1] + mColorRadius.val[1];

        mLowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
        mUpperBound.val[2] = hsvColor.val[2] + mColorRadius.val[2];

        mLowerBound.val[3] = 0;
        mUpperBound.val[3] = 255;

        Mat spectrumHsv = new Mat(1, (int)(maxH-minH), CvType.CV_8UC3);

        for (int j = 0; j < maxH-minH; j++) {
            byte[] tmp = {(byte)(minH+j), (byte)255, (byte)255};
            spectrumHsv.put(0, j, tmp);
        }

        Imgproc.cvtColor(spectrumHsv, mSpectrum, Imgproc.COLOR_HSV2RGB_FULL, 4);
    }

    public Mat getSpectrum() {
        return mSpectrum;
    }

    public void setMinContourArea(double area) {
        mMinContourArea = area;
    }

    public void process(Mat rgbaImage) {
        Imgproc.pyrDown(rgbaImage, mPyrDownMat);
        Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);

        Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);

        Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);
        Imgproc.dilate(mMask, mDilatedMask, new Mat());

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Imgproc.findContours(mDilatedMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Find max contour area
        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea)
                maxArea = area;
        }

        // Filter contours by area and resize to fit the original image size
        mContours.clear();
        each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            if (Imgproc.contourArea(contour) > mMinContourArea*maxArea) {
                Core.multiply(contour, new Scalar(4,4), contour);
                mContours.add(contour);
            }
        }
    }

    // Todo

    public DetectedCircle processCircleTest(Mat rgbaImage, DetectedCircle mOuterCircle){

        DetectedCircle mDetectedInnerCircle= new DetectedCircle();
        Imgproc.medianBlur(rgbaImage,rgbaImage,3);
        // Convert input image to HSV
        Mat hsv_image= new Mat();
        Imgproc.cvtColor(rgbaImage, hsv_image, Imgproc.COLOR_RGB2HSV);

        // Threshold the HSV image, keep only the red pixels
        Mat lower_red_hue_range=new Mat();
        Mat upper_red_hue_range=new Mat();
        Core.inRange(hsv_image, new Scalar(0, 100, 100), new Scalar(10, 255, 255), lower_red_hue_range);
        Core.inRange(hsv_image, new Scalar(160, 100, 100), new Scalar(179, 255, 255), upper_red_hue_range);

        // Combine the above two images
        Mat red_hue_image=new Mat();
        Core.addWeighted(lower_red_hue_range, 1.0, upper_red_hue_range, 1.0, 0.0, red_hue_image);

        Imgproc.GaussianBlur(red_hue_image, red_hue_image, new Size(19,19), 2, 2);

        // Use the Hough transform to detect circles in the combined threshold image
        Mat circles=new Mat();
        Mat blk=new Mat();
        //Imgproc.cvtColor(red_hue_image,rgbaImage,Imgproc.COLOR_HSV2BGR_FULL);
        //Imgproc.cvtColor(blk,blk,Imgproc.COLOR_BGR2GRAY);
        Imgproc.HoughCircles(red_hue_image, circles, CV_HOUGH_GRADIENT, 1, red_hue_image.rows()/8, 100, 20, 0, 0);
        Point pt=new Point();
        int radius=0;
        for (int x = 0; x < circles.cols(); x++) {
            double vCircle[] = circles.get(0, x);
            if (vCircle == null)
                break;
            pt = new Point(Math.round(vCircle[0]),
                    Math.round(vCircle[1]));
            radius = (int) Math.round(vCircle[2]);
//            if(radius > 10) {
//                Log.d("cv: center: ", pt + " &  radius " + radius);
//
//            }
        }
        if(radius > INNER_CIRCLE_MIN_RADIUS && radius < mOuterCircle.getCirRadius()) {
            mDetectedInnerCircle.setCirCoordinate(pt);
            mDetectedInnerCircle.setCirRadius(radius);
            mDetectedInnerCircle.setCircle(true);
            Log.d("cv:center_in: ", pt + " &  radius_in " + radius);
            //Imgproc.circle(rgbaImage, pt, radius, new Scalar(0, 100, 255), 3);
        }
        return mDetectedInnerCircle;
    }




    public DetectedCircle processCircleHough(Mat rgbaImage) {


//        Imgproc.medianBlur(rgbaImage,rgbaImage,3);
//        // Convert input image to HSV
//        Mat hsv_image= new Mat();
//        Imgproc.cvtColor(rgbaImage, hsv_image, Imgproc.COLOR_RGB2HSV);
//
//        // Threshold the HSV image, keep only the red pixels
//        Mat lower_red_hue_range=new Mat();
//        Mat upper_red_hue_range=new Mat();
//        Core.inRange(hsv_image, new Scalar(0, 100, 100), new Scalar(10, 255, 255), lower_red_hue_range);
//        Core.inRange(hsv_image, new Scalar(160, 100, 100), new Scalar(179, 255, 255), upper_red_hue_range);
//
//        // Combine the above two images
//        Mat red_hue_image=new Mat();
//        Core.addWeighted(lower_red_hue_range, 1.0, upper_red_hue_range, 1.0, 0.0, red_hue_image);
//
//        Imgproc.GaussianBlur(red_hue_image, red_hue_image, new Size(19,19), 2, 2);
//
//        // Use the Hough transform to detect circles in the combined threshold image
//        Mat circles=new Mat();
//        Mat blk=new Mat();
//        //Imgproc.cvtColor(red_hue_image,rgbaImage,Imgproc.COLOR_HSV2BGR_FULL);
//        //Imgproc.cvtColor(blk,blk,Imgproc.COLOR_BGR2GRAY);
//        Imgproc.HoughCircles(red_hue_image, circles, CV_HOUGH_GRADIENT, 1, red_hue_image.rows()/8, 100, 20, 0, 0);

        DetectedCircle mDetectedOuterCircle= new DetectedCircle();
        Mat blak_image= new Mat();
        Imgproc.cvtColor(rgbaImage, blak_image, Imgproc.COLOR_RGB2GRAY);

        Imgproc.GaussianBlur(blak_image, blak_image, new Size(7,7), 2, 2);
        Imgproc.adaptiveThreshold(blak_image, blak_image, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 15, 17);
        Mat circles=new Mat();
        Mat blk=new Mat();
        Imgproc.HoughCircles(blak_image, circles, CV_HOUGH_GRADIENT, 1, blak_image.rows()/2, 100, 35,0,0);


        int detectedCircleCount=0;
        //Point[] mMultCirclePoint= new Point[circles.cols()];
        double mCirCoX=0.0f;
        double mCirCoY=0.0f;
        int mCirRadius=0;

        int cirCount=circles.cols();
        for (int x = 0; x < circles.cols(); x++) {

            double vCircle[] = circles.get(0, x);
            if (vCircle == null)
                break;
            Point pt = new Point(Math.round(vCircle[0]),   Math.round(vCircle[1]));
            int radius = (int) Math.round(vCircle[2]);
            Imgproc.circle(rgbaImage, pt, radius, new Scalar(100, 100, 255), 3);
            if(radius > OUTER_CIRCLE_MIN_RADIUS) {
                mCirCoX+=vCircle[0];
                mCirCoY += vCircle[1];
                mCirRadius+=radius;
                detectedCircleCount++;

                //Imgproc.circle(rgbaImage, pt, radius, new Scalar(0, 0, 255), 3);
            }
        }
        if(detectedCircleCount > 0) {
            mDetectedOuterCircle.setCirCoordinate(new Point((mCirCoX / detectedCircleCount), (mCirCoY / detectedCircleCount)));
            mDetectedOuterCircle.setCirRadius(mCirRadius / detectedCircleCount);
            mDetectedOuterCircle.setCircle(true);
            Log.d("cv:center_out: ", mDetectedOuterCircle.getCirCoordinate() + " &  radius_out " + mDetectedOuterCircle.getCirRadius());
            //Imgproc.circle(rgbaImage, mDetectedOuterCircle.getCirCoordinate(), mDetectedOuterCircle.getCirRadius(), new Scalar(0, 0, 255), 3);
        }
        return  mDetectedOuterCircle;

    }

    public DetectedCircle processBlackCircle(Mat rgbaImage) {


        DetectedCircle mDetectedInnerCircle= new DetectedCircle();
        Imgproc.medianBlur(rgbaImage,rgbaImage,3);
        // Convert input image to HSV
        Mat hsv_image= new Mat();
        Imgproc.cvtColor(rgbaImage, hsv_image, Imgproc.COLOR_RGB2HSV);

        // Threshold the HSV image, keep only the red pixels
        Mat lower_red_hue_range=new Mat();
        Mat upper_red_hue_range=new Mat();
        Core.inRange(hsv_image, new Scalar(100, 0, 100), new Scalar(255, 10, 255), lower_red_hue_range);
        Core.inRange(hsv_image, new Scalar(160, 100, 100), new Scalar(255, 179, 255), upper_red_hue_range);

        // Combine the above two images
        Mat red_hue_image=new Mat();
        Core.addWeighted(lower_red_hue_range, 1.0, upper_red_hue_range, 1.0, 0.0, red_hue_image);

        Imgproc.GaussianBlur(red_hue_image, red_hue_image, new Size(19,19), 2, 2);

        // Use the Hough transform to detect circles in the combined threshold image
        Mat circles=new Mat();
        Mat blk=new Mat();
        //Imgproc.cvtColor(red_hue_image,rgbaImage,Imgproc.COLOR_HSV2BGR_FULL);
        //Imgproc.cvtColor(blk,blk,Imgproc.COLOR_BGR2GRAY);
        Imgproc.HoughCircles(red_hue_image, circles, CV_HOUGH_GRADIENT, 1, red_hue_image.rows()/8, 100, 20, 0, 0);
        Point pt=new Point();
        int radius=0;
        for (int x = 0; x < circles.cols(); x++) {
            double vCircle[] = circles.get(0, x);
            if (vCircle == null)
                break;
            pt = new Point(Math.round(vCircle[0]),
                    Math.round(vCircle[1]));
            radius = (int) Math.round(vCircle[2]);
//            if(radius > 10) {
//                Log.d("cv: center: ", pt + " &  radius " + radius);
//
//            }
        }
        if(radius > INNER_CIRCLE_MIN_RADIUS) {
            mDetectedInnerCircle.setCirCoordinate(pt);
            mDetectedInnerCircle.setCirRadius(radius);
            mDetectedInnerCircle.setCircle(true);
            Log.d("cv:center_in: ", pt + " &  radius_in " + radius);
            Imgproc.circle(rgbaImage, pt, radius, new Scalar(0, 255, 255), 3);
        }
        return mDetectedInnerCircle;
    }

    public void drawCalibLine(Mat srcImg){
        int imgHeight=srcImg.height();
        int imgWidth=srcImg.width();
        Scalar colorVal= new Scalar(255,255,255);
        Imgproc.line(srcImg,new Point(0,imgHeight/2),new Point(imgWidth,imgHeight/2), colorVal,1);
        Imgproc.line(srcImg,new Point(imgWidth/2,0),new Point(imgWidth/2,imgHeight), colorVal,1);
    }
    public void saveTargetImage(Mat mSavedImg){
        File sdRoot = Environment.getExternalStorageDirectory();
        if (! sdRoot.exists()){
            if (! sdRoot.mkdirs()){
            }
        }

        String dir = "/DCIM/DirtHit/";
        String fileName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()).toString() + ".jpg";
        File mkDir = new File(sdRoot, dir);
        mkDir.mkdirs();
        File pictureFile = new File(sdRoot, dir + fileName);

        Imgcodecs.imwrite("/sdcard/" + dir + fileName,mSavedImg);

    }
    public List<MatOfPoint> getContours() {
        return mContours;
    }
}
