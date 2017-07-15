package fim.uni_passau.de.countyourhits.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.peak.salut.Callbacks.SalutCallback;
import com.peak.salut.Callbacks.SalutDataCallback;
import com.peak.salut.Callbacks.SalutDeviceCallback;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutDevice;
import com.peak.salut.SalutServiceData;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Random;

import fim.uni_passau.de.countyourhits.R;
import fim.uni_passau.de.countyourhits.app.Helper;
import fim.uni_passau.de.countyourhits.model.DetectedCircle;
import fim.uni_passau.de.countyourhits.util.ColorBlobDetector;

public class ColorBlobDetectionActivity extends Activity implements OnTouchListener, CvCameraViewListener2, SalutDataCallback {

    //A Tag to filter the log messages
    private static final String TAG = "OCVSample::Activity";

    private static final int SBAR_THRESHOLD_BLOCK_SIZE_MAX = 100;
    private static final int SBAR_THRESHOLD_BLOCK_SIZE_MIN = 10;

    private static final int SBAR_THRESHOLD_C_MAX = 100;
    private static final int SBAR_THRESHOLD_C_MIN = 10;

    private static final int CALIB_CENTER_POINT_THRESHOLD = 30;
    private static final double CALIB_CENTER_POINT_LOW_THRESHOLD = 5.0;

    private boolean mIsColorSelected = false;

    private boolean mIsStableCenterPoint = false;
    private int mStableCenterPointCount = 0;

    private Mat mRgba;
    private Scalar mBlobColorRgba;
    private Scalar mBlobColorHsv;
    private ColorBlobDetector mDetector;
    private Mat mSpectrum;
    private Size SPECTRUM_SIZE;
    private Scalar CONTOUR_COLOR;

    private Point centerPoint;
    ArrayList<Mat> detectCenterPoint = new ArrayList<>();
    private int frameCount = 0;
    private static final int cPointFrameCount = 5;
    public DetectedCircle mOuterCircle = new DetectedCircle();
    //slaute object
    public SalutDataReceiver dataReceiver;
    public SalutServiceData serviceData;
    public Salut network;

    private SeekBar mOutThrshldBlockSize, mOutThrshldC;
    private SeekBar mInThrshldBlockSize, mInThrshldC;

    private TextView mTxtOutPBarBlock, mTxtOutPBarC, mTxtInPBarBlock, mTxtInPBarC;

    private ProgressBar mProgbarCamera;

    public static ArrayList<DetectedCircle> mInnerCircleList = new ArrayList<>();
    //A class used to implement the interaction between OpenCV and the device camera.
    private CameraBridgeViewBase mOpenCvCameraView;

    //This is the callback object used when we initialize the OpenCV library asynchronously.
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        //This is the callback method called once the OpenCV manager is connected
        public void onManagerConnected(int status) {
            switch (status) {
                //Once the OpenCV manager is successfully connected we can enable the
                //camera interaction with the defined OpenCV camera view
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(ColorBlobDetectionActivity.this);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public ColorBlobDetectionActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // for Full Screen of Activity
        setContentView(R.layout.activity_color_blob_detection); // setting the layout file


        initControl();
        /*initSalutService();
        setupNetwork();*/


        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view); // id for OpenCV java camera view
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE); //Set the view as visible
        //Register your activity as the callback object to handle camera frames
        mOpenCvCameraView.setCvCameraViewListener(this);

    }

    private void initControl() {

        mOutThrshldBlockSize = (SeekBar) findViewById(R.id.sbar_block_size);
        mOutThrshldC = (SeekBar) findViewById(R.id.sbar_thres_c);

        mInThrshldBlockSize = (SeekBar) findViewById(R.id.sbar_inner_block_size);
        mInThrshldC = (SeekBar) findViewById(R.id.sbar_inner_thres_c);

        mOutThrshldBlockSize.setMax(SBAR_THRESHOLD_BLOCK_SIZE_MAX - SBAR_THRESHOLD_BLOCK_SIZE_MIN);
        mOutThrshldBlockSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= mOutThrshldBlockSize.getProgress() && progress % 2 != 0) {
                    mOutThrshldBlockSize.setProgress(progress);
                    mTxtOutPBarBlock.setText(String.valueOf(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mOutThrshldC.setMax(SBAR_THRESHOLD_C_MAX - SBAR_THRESHOLD_C_MIN);
        mOutThrshldC.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= mOutThrshldC.getProgress()) {
                    mOutThrshldC.setProgress(progress);
                    mTxtOutPBarC.setText(String.valueOf(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mInThrshldBlockSize.setMax(SBAR_THRESHOLD_BLOCK_SIZE_MAX - SBAR_THRESHOLD_BLOCK_SIZE_MIN);
        mInThrshldBlockSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= mInThrshldBlockSize.getProgress() && progress % 2 != 0) {
                    mInThrshldBlockSize.setProgress(progress);
                    mTxtInPBarBlock.setText(String.valueOf(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mInThrshldC.setMax(SBAR_THRESHOLD_C_MAX - SBAR_THRESHOLD_C_MIN);
        mInThrshldC.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= mInThrshldC.getProgress()) {
                    mInThrshldC.setProgress(progress);
                    mTxtInPBarC.setText(String.valueOf(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        mProgbarCamera = (ProgressBar) findViewById(R.id.pbar_camera);
        mProgbarCamera.setVisibility(View.GONE);

        mTxtInPBarBlock = (TextView) findViewById(R.id.txt_inner_block_size);
        mTxtInPBarC = (TextView) findViewById(R.id.txt_inner_thres_C);
        mTxtOutPBarBlock = (TextView) findViewById(R.id.txt_outer_block_size);
        mTxtOutPBarC = (TextView) findViewById(R.id.txt_outer_thres_c);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            //Call the async initialization and pass the callback object we
            //created later, and chose which version of OpenCV library to
            //load. Just make sure that the OpenCV manager you installed
            //supports the version you are trying to load.
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        if (network != null) {
            if (network.isRunningAsHost) {
                network.stopNetworkService(false);
            }
        }
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4); // this means that the matrix will hold 8-bit unsigned characters for color intensity with four channel.

        /**CV_(Data type size [“8” | “16” | “32” | “64”])([“S” | “U” | “F” , for signed, unsigned
         integers, or floating point numbers])(Number of channels[“C1 | C2 | C3 | C4”, for one,
         two, three, or four channels respectively]) **/
        mDetector = new ColorBlobDetector();
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        SPECTRUM_SIZE = new Size(200, 64);
        CONTOUR_COLOR = new Scalar(255, 0, 0, 255);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    public boolean onTouch(View v, MotionEvent event) {
        int cols = mRgba.cols();
        int rows = mRgba.rows();

        int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;
        int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;

        int x = (int) event.getX() - xOffset;
        int y = (int) event.getY() - yOffset;

        Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");

        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

        Rect touchedRect = new Rect();

        touchedRect.x = (x > 4) ? x - 4 : 0;
        touchedRect.y = (y > 4) ? y - 4 : 0;

        touchedRect.width = (x + 4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
        touchedRect.height = (y + 4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

        Mat touchedRegionRgba = mRgba.submat(touchedRect);

        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Calculate average color of touched region
        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width * touchedRect.height;
        for (int i = 0; i < mBlobColorHsv.val.length; i++)
            mBlobColorHsv.val[i] /= pointCount;

        mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);

        Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
                ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");

        mDetector.setHsvColor(mBlobColorHsv);

        Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);

        mIsColorSelected = true;

        touchedRegionRgba.release();
        touchedRegionHsv.release();

        return false; // don't need subsequent touch events
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba(); // retrieving the full camera frame using this method
        int thrsBlck = 0, thrsC = 0;

        ArrayList<DetectedCircle> mInnerCircle;
        //mIsColorSelected=true;
        if (mIsColorSelected) {
            Log.d(TAG, "OuterCircleIsStable " + mIsStableCenterPoint);
            if (!mIsStableCenterPoint ) {
                if(frameCount <= cPointFrameCount){
                    detectCenterPoint.add(mRgba);
                    frameCount++;

                }
                else {
                    mOuterCircle = initStableCenterPoint(detectCenterPoint);
                    frameCount=0;
                }
            }
            else {
                if (mOuterCircle != null && mOuterCircle.isCircle() && mIsStableCenterPoint) {
                    Log.d(TAG, "OuterCircle " + mOuterCircle.getCirCoordinate() +" radius : " + mOuterCircle.getCirRadius());
                    Imgproc.circle(mRgba, mOuterCircle.getCirCoordinate(), mOuterCircle.getCirRadius(), new Scalar(0, 255, 100), 5);
                    mDetector.saveTargetImage(mRgba);
                    boolean IsPassed = IsPassedHighThreshold(mRgba, mOuterCircle);
                    if (IsPassed) {
                        Log.d(TAG, "OuterCirclePassed " + mOuterCircle.getCirCoordinate() +" radius : " + mOuterCircle.getCirRadius());
                        thrsBlck = (mInThrshldBlockSize.getProgress() % 2 == 0) ? mInThrshldBlockSize.getProgress() + 1 : mInThrshldBlockSize.getProgress();
                        thrsBlck += SBAR_THRESHOLD_BLOCK_SIZE_MIN;
                        thrsC = SBAR_THRESHOLD_C_MIN + mInThrshldC.getProgress();
                        mInnerCircle = mDetector.processDartCircle(mRgba, mOuterCircle, thrsBlck, thrsC);

                        //Toast.makeText(getApplicationContext(), "Inner Circle Search", Toast.LENGTH_SHORT).show();
                        if (mInnerCircle != null) {
                            int numberInrCircle = 0;
                            while (numberInrCircle < mInnerCircle.size()) {
                                Log.d(TAG, "OuterCirclePassedInnerCircle " + mInnerCircle.get(numberInrCircle).getCirCoordinate() +" radius : " + mInnerCircle.get(0).getCirRadius());
                                double mCircleDistance = calculateDistance(mInnerCircle.get(numberInrCircle).getCirCoordinate(), mOuterCircle.getCirCoordinate());
                                Log.d(TAG, "OuterCirclePassedInnerCircleDistance " + mCircleDistance);
                                if (mCircleDistance <= mOuterCircle.getCirRadius()) {
                                    double dartDistnc=calculateDistance( mOuterCircle.getCirCoordinate(), mInnerCircle.get(numberInrCircle).getCirCoordinate());
                                    Imgproc.circle(mRgba, mInnerCircle.get(numberInrCircle).getCirCoordinate(), mInnerCircle.get(numberInrCircle).getCirRadius(), new Scalar(80, 200, 255), 3);
                                    Imgproc.line(mRgba, mOuterCircle.getCirCoordinate(), mInnerCircle.get(numberInrCircle).getCirCoordinate(), new Scalar(255, 255, 255), 3);
                                    //Imgproc.putText(mRgba, Helper.convertDouble2String(mCircleDistance), mInnerCircle.get(numberInrCircle).getCirCoordinate(), Core.FONT_HERSHEY_PLAIN, 1.0, new Scalar(255, 255, 255));
                                    Imgproc.putText(mRgba, Helper.convertDouble2String(calculateScore(mOuterCircle.getCirRadius(),dartDistnc)), mInnerCircle.get(numberInrCircle).getCirCoordinate(), Core.FONT_HERSHEY_PLAIN, 1.0, new Scalar(255, 255, 255));
                                    //mInnerCircleList.add(mInnerCircle.get(numberInrCircle));
                                    Log.d(TAG, "OuterCirclePassedInnerCircleScore "+ mOuterCircle.getCirRadius() +", "+ dartDistnc+" , " + calculateScore(mOuterCircle.getCirRadius(),mInnerCircle.get(numberInrCircle).getCirRadius()) );
                                }
                                numberInrCircle++;
                            }
                        }
                    } else {
                        mIsStableCenterPoint = false;
                        frameCount=0;
                    }
                }
            }
            /*thrsBlck= (mOutThrshldBlockSize.getProgress() % 2 == 0) ? mOutThrshldBlockSize.getProgress() + 1 : mOutThrshldBlockSize.getProgress();
            thrsBlck += SBAR_THRESHOLD_BLOCK_SIZE_MIN;
            thrsC=SBAR_THRESHOLD_C_MIN + mOutThrshldC.getProgress();
            Log.d(TAG, "onCameraFrame: threshold_block " + thrsBlck + ", thrs_C "+thrsC);
            mOuterCircle = mDetector.processCircleByColor(mRgba,new Scalar(0,255,255),thrsBlck,thrsC);
            Log.d(TAG, "onCameraFrame: mOuterCircle " + mOuterCircle.isCircle());

            if (mOuterCircle != null && mOuterCircle.isCircle() && mIsStableCenterPoint) {
                thrsBlck= (mInThrshldBlockSize.getProgress() % 2 == 0) ? mInThrshldBlockSize.getProgress() + 1 : mInThrshldBlockSize.getProgress();
                thrsBlck += SBAR_THRESHOLD_BLOCK_SIZE_MIN;
                thrsC=SBAR_THRESHOLD_C_MIN + mInThrshldC.getProgress();
                mInnerCircle = mDetector.processDartCircle(mRgba, mOuterCircle,thrsBlck,thrsC);

                if (mInnerCircle != null) {
                    int numberInrCircle=0;
                    while (numberInrCircle < mInnerCircle.size()){
                        double mCircleDistance =calculateDistance(mInnerCircle.get(numberInrCircle).getCirCoordinate(),mOuterCircle.getCirCoordinate());
                        Log.d(TAG, "Distance between " + mCircleDistance);
                        if (mCircleDistance <= mOuterCircle.getCirRadius()) {
                            Imgproc.circle(mRgba, mInnerCircle.get(numberInrCircle).getCirCoordinate(), mInnerCircle.get(numberInrCircle).getCirRadius(), new Scalar(80, 200, 255), 3);
                            Imgproc.line(mRgba, mOuterCircle.getCirCoordinate(), mInnerCircle.get(numberInrCircle).getCirCoordinate(), new Scalar(255, 255, 255), 3);
                            Imgproc.putText(mRgba, Helper.convertDouble2String(mCircleDistance), mInnerCircle.get(numberInrCircle).getCirCoordinate(), Core.FONT_HERSHEY_PLAIN, 1.0, new Scalar(255, 255, 255));
                            mInnerCircleList.add(mInnerCircle.get(numberInrCircle));
                        }
                        numberInrCircle++;
                    }


                }
            }*/
    }

    return mRgba;
}

    private boolean IsPassedHighThreshold(Mat rgb, DetectedCircle circle){
        boolean IsPassed=false;
        int thrsBlck = 0, thrsC = 0;
        DetectedCircle mOuterCircle = new DetectedCircle();

         thrsBlck = (mOutThrshldBlockSize.getProgress() % 2 == 0) ? mOutThrshldBlockSize.getProgress() + 1 : mOutThrshldBlockSize.getProgress();
        thrsBlck += SBAR_THRESHOLD_BLOCK_SIZE_MIN;
         thrsC = SBAR_THRESHOLD_C_MIN + mOutThrshldC.getProgress();
        Log.d(TAG, "onCameraFrame:IsPassedHighThreshold threshold_block " + thrsBlck + ", thrs_C "+thrsC);
        mOuterCircle = mDetector.processCircleByColor(rgb,new Scalar(0,255,255),thrsBlck,thrsC);

        double distDiff= calculateDistance(circle.getCirCoordinate(), mOuterCircle.getCirCoordinate());
        if(distDiff < CALIB_CENTER_POINT_THRESHOLD){
            IsPassed=true;
        }
        else{
            IsPassed=false;
            mIsStableCenterPoint=false;
        }

        return IsPassed;
    }
    private double calculateDistance(Point CorX, Point CorY) {
        return Math.sqrt(Math.pow((CorX.x - CorY.x), 2) +
                Math.pow((CorX.y - CorY.y), 2));
    }

    private double calculateScore(double trgtRadius, double center2dartRadius){
        double score= (center2dartRadius / trgtRadius) * 100;
        return score;
    }
    private DetectedCircle initStableCenterPoint(ArrayList<Mat> circleImg) {
        DetectedCircle mOuterCircle = new DetectedCircle();
        //mProgbarCamera.setVisibility(View.VISIBLE);
        ArrayList<DetectedCircle> circleList = new ArrayList<>();

        double mCirCoX = 0.0f;
        double mCirCoY = 0.0f;
        int mCirRadius = 0;

        int thrsBlck = (mOutThrshldBlockSize.getProgress() % 2 == 0) ? mOutThrshldBlockSize.getProgress() + 1 : mOutThrshldBlockSize.getProgress();
        thrsBlck += SBAR_THRESHOLD_BLOCK_SIZE_MIN;
        int thrsC = SBAR_THRESHOLD_C_MIN + mOutThrshldC.getProgress();
        int count = 0;
        while (count < circleImg.size()) {
            mOuterCircle = mDetector.processCircleByColor(circleImg.get(count), new Scalar(0, 255, 255), thrsBlck, thrsC);
            circleList.add(mOuterCircle);
            mCirCoX += mOuterCircle.getCirCoordinate().x;
            mCirCoY += mOuterCircle.getCirCoordinate().y;
            mCirRadius += mOuterCircle.getCirRadius();
            count++;
        }
        Random rndm= new Random();
        int rndCircleNumber= rndm.nextInt(cPointFrameCount);
        Point newCalibPoint = new Point(mCirCoX / count, mCirCoY / count);
        Point randomPoint = circleList.get(rndCircleNumber).getCirCoordinate();
        double pointsDiff=calculateDistance(newCalibPoint, randomPoint);
        if (pointsDiff < CALIB_CENTER_POINT_LOW_THRESHOLD) {
            mIsStableCenterPoint = true;
            mOuterCircle = new DetectedCircle();
            mOuterCircle.setCirCoordinate(newCalibPoint);
            mOuterCircle.setCirRadius(mCirRadius / count);
            mOuterCircle.setCircle(true);
        } else {
            mIsStableCenterPoint=false;
            mOuterCircle.setCircle(false);
        }


        Log.i(TAG, "Random value: "+ rndCircleNumber + "| Difference : " + pointsDiff +" | IswithinThreshold : "+ mIsStableCenterPoint );
        //mProgbarCamera.setVisibility(View.GONE);

        return mOuterCircle;
    }

    private void initSalutService() {
        dataReceiver = new SalutDataReceiver(this, this);
        /*Populate the details for our awesome service. */
        serviceData = new SalutServiceData("wifiservice", 13334, "P2P");

        /*Create an instance of the Salut class, with all of the necessary data from before.
        * We'll also provide a callback just in case a device doesn't support WiFi Direct, which
        * Salut will tell us about before we start trying to use methods.*/
        network = new Salut(dataReceiver, serviceData, new SalutCallback() {
            @Override
            public void call() {
                // wiFiFailureDialog.show();
                // OR
                Log.e(TAG, "Sorry, but this device does not support WiFi Direct.");
            }
        });
    }

    private void setupNetwork() {
        if (!network.isRunningAsHost) {
            network.startNetworkService(new SalutDeviceCallback() {
                @Override
                public void call(SalutDevice salutDevice) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ColorBlobDetectionActivity.this);
                    alertDialog.setTitle("Host Device Connected")
                            .setMessage("Device: " + salutDevice.deviceName + " connected as client")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = alertDialog.create();
                    alertDialog.setTitle("STOP Discovery");
                    alert.show();
                    Log.e(TAG, "Device: " + salutDevice.instanceName);

                }
            }, new SalutCallback() {
                @Override
                public void call() {
                    Toast.makeText(getApplicationContext(), "Device: Hosting is sucessful.", Toast.LENGTH_SHORT).show();
                    mOpenCvCameraView.enableView();
                }
            }, new SalutCallback() {
                @Override
                public void call() {
                    Toast.makeText(getApplicationContext(), "Device: Hosting is unsucessful.", Toast.LENGTH_SHORT).show();
                    showDialog();


                }
            });

        } else {
            //stopHost();
        }
    }

    protected void stopHost() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ColorBlobDetectionActivity.this);
        alertDialog.setMessage("Do you want to stop Host Service ?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        network.stopNetworkService(true);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialog.create();
        alertDialog.setTitle("Stop Service");
        alert.show();
    }

    @Override
    public void onDataReceived(Object o) {

    }

    private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }


    public void showDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(ColorBlobDetectionActivity.this).create();
        alertDialog.setTitle("Device is not hosted:");
        alertDialog.setMessage("Check internet connection and reconnect");

        // Alert dialog button
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Reconnect",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setupNetwork();
                        dialog.dismiss();// use dismiss to cancel alert dialog
                    }
                });
        alertDialog.show();
    }

}
