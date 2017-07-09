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
import android.widget.SeekBar;
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
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

import fim.uni_passau.de.countyourhits.R;
import fim.uni_passau.de.countyourhits.app.Helper;
import fim.uni_passau.de.countyourhits.model.DetectedCircle;
import fim.uni_passau.de.countyourhits.util.ColorBlobDetector;

public class ColorBlobDetectionActivity extends Activity implements OnTouchListener, CvCameraViewListener2 ,SalutDataCallback {

    //A Tag to filter the log messages
    private static final String TAG = "OCVSample::Activity";

    private static final int SBAR_THRESHOLD_BLOCK_SIZE_MAX=100;
    private static final int SBAR_THRESHOLD_BLOCK_SIZE_MIN=10;

    private static final int SBAR_THRESHOLD_C_MAX=100;
    private static final int SBAR_THRESHOLD_C_MIN=10;

    private boolean mIsColorSelected = false;
    private Mat mRgba;
    private Scalar mBlobColorRgba;
    private Scalar mBlobColorHsv;
    private ColorBlobDetector mDetector;
    private Mat mSpectrum;
    private Size SPECTRUM_SIZE;
    private Scalar CONTOUR_COLOR;

    //slaute object
    public SalutDataReceiver dataReceiver;
    public SalutServiceData serviceData;
    public Salut network;

    private SeekBar mOutThrshldBlockSize, mOutThrshldC;
    private SeekBar mInThrshldBlockSize, mInThrshldC;

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
                    //mOpenCvCameraView.enableView();
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
        initSalutService();
        setupNetwork();



        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view); // id for OpenCV java camera view
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE); //Set the view as visible
        //Register your activity as the callback object to handle camera frames
        mOpenCvCameraView.setCvCameraViewListener(this);

    }

    private void initControl(){

        mOutThrshldBlockSize =(SeekBar) findViewById(R.id.sbar_block_size);
        mOutThrshldC =(SeekBar) findViewById(R.id.sbar_thres_c);

        mInThrshldBlockSize =(SeekBar) findViewById(R.id.sbar_inner_block_size);
        mInThrshldC =(SeekBar) findViewById(R.id.sbar_inner_thres_c);

        mOutThrshldBlockSize.setMax(SBAR_THRESHOLD_BLOCK_SIZE_MAX - SBAR_THRESHOLD_BLOCK_SIZE_MIN);
        mOutThrshldBlockSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress <= mOutThrshldBlockSize.getProgress() && progress %2 !=0){
                    mOutThrshldBlockSize.setProgress(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        mOutThrshldC.setMax(SBAR_THRESHOLD_C_MAX - SBAR_THRESHOLD_C_MIN);
        mOutThrshldC.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress <= mOutThrshldC.getProgress()){
                    mOutThrshldC.setProgress(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mInThrshldBlockSize.setMax(SBAR_THRESHOLD_BLOCK_SIZE_MAX- SBAR_THRESHOLD_BLOCK_SIZE_MIN);
        mInThrshldBlockSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress <= mInThrshldBlockSize.getProgress() && progress %2 !=0){
                    mInThrshldBlockSize.setProgress(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mInThrshldC.setMax(SBAR_THRESHOLD_C_MAX- SBAR_THRESHOLD_C_MIN);
        mInThrshldC.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress <= mInThrshldC.getProgress()){
                    mInThrshldC.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
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

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        if(network != null) {
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

        if (mIsColorSelected) {
            DetectedCircle mOuterCircle;
            DetectedCircle mOuterWhiteCircle;
            ArrayList<DetectedCircle> mInnerCircle;
            //mDetector.drawCalibLine(mRgba);
            //mOuterWhiteCircle = mDetector.processWhiteCircleHough(mRgba);
            //Imgproc.circle(mRgba, mOuterWhiteCircle.getCirCoordinate(), mOuterWhiteCircle.getCirRadius(), new Scalar(0, 0, 255), 2);
//            mOuterCircle = mDetector.processCircleHough(mRgba);
            int thrsBlck= (mOutThrshldBlockSize.getProgress() % 2 == 0) ? mOutThrshldBlockSize.getProgress() + 1 : mOutThrshldBlockSize.getProgress();
            thrsBlck += SBAR_THRESHOLD_BLOCK_SIZE_MIN;
            int thrsC=SBAR_THRESHOLD_C_MIN + mOutThrshldC.getProgress();
            Log.d(TAG, "onCameraFrame: threshold_block " + thrsBlck + ", thrs_C "+thrsC);
            mOuterCircle = mDetector.processCircleByColor(mRgba,new Scalar(0,255,255),thrsBlck,thrsC);
            Log.d(TAG, "onCameraFrame: mOuterCircle " + mOuterCircle.isCircle());

            if (mOuterCircle != null && mOuterCircle.isCircle()) {
                //mInnerCircle= mDetector.processBlackCircle(mRgba);
                //Imgproc.circle(mRgba, mOuterCircle.getCirCoordinate(), mOuterCircle.getCirRadius(), new Scalar(0, 0, 255), 2);
                thrsBlck= (mInThrshldBlockSize.getProgress() % 2 == 0) ? mInThrshldBlockSize.getProgress() + 1 : mInThrshldBlockSize.getProgress();
                thrsBlck += SBAR_THRESHOLD_BLOCK_SIZE_MIN;
                thrsC=SBAR_THRESHOLD_C_MIN + mInThrshldC.getProgress();
                mInnerCircle = mDetector.processDartCircle(mRgba, mOuterCircle,thrsBlck,thrsC);
                //Log.d(TAG, "mInnerCircle: " + mInnerCircle.getCirCoordinate() + " radius: " + mInnerCircle.getCirRadius());

                if (mInnerCircle != null) {
                    int numberInrCircle=0;
                    while (numberInrCircle < mInnerCircle.size()){

                        double mCircleDistance = Math.sqrt(Math.pow((mInnerCircle.get(numberInrCircle).getCirCoordinate().x - mOuterCircle.getCirCoordinate().x), 2) +
                                Math.pow((mInnerCircle.get(numberInrCircle).getCirCoordinate().y - mOuterCircle.getCirCoordinate().y), 2));


                        Log.d(TAG, "Distance between " + mCircleDistance);

                        if (mCircleDistance <= mOuterCircle.getCirRadius()) {

                            Imgproc.circle(mRgba, mInnerCircle.get(numberInrCircle).getCirCoordinate(), mInnerCircle.get(numberInrCircle).getCirRadius(), new Scalar(80, 200, 255), 3);
                            Imgproc.line(mRgba, mOuterCircle.getCirCoordinate(), mInnerCircle.get(numberInrCircle).getCirCoordinate(), new Scalar(255, 255, 255), 3);
                            Imgproc.putText(mRgba, Helper.convertDouble2String(mCircleDistance), mInnerCircle.get(numberInrCircle).getCirCoordinate(), Core.FONT_HERSHEY_PLAIN, 1.0, new Scalar(255, 255, 255));

                            mInnerCircleList.add(mInnerCircle.get(numberInrCircle));
                        } else {

                        }
                        numberInrCircle++;
                    }


                }
            }
        }
        return mRgba;
    }
    private void initSalutService(){
        dataReceiver = new SalutDataReceiver(this, this);
        /*Populate the details for our awesome service. */
        serviceData = new SalutServiceData("wifiservice", 13334,"P2P");

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
    private void setupNetwork()    {
        if(!network.isRunningAsHost)
        {
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



    public void showDialog(){
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
