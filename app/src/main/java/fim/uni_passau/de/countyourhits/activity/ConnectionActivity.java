package fim.uni_passau.de.countyourhits.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.peak.salut.Callbacks.SalutCallback;
import com.peak.salut.Callbacks.SalutDataCallback;
import com.peak.salut.Callbacks.SalutDeviceCallback;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutDevice;
import com.peak.salut.SalutServiceData;

import java.io.File;
import java.io.IOException;

import fim.uni_passau.de.countyourhits.R;
import fim.uni_passau.de.countyourhits.app.Helper;
import fim.uni_passau.de.countyourhits.model.ResultResponse;

public class ConnectionActivity extends AppCompatActivity  implements SalutDataCallback, View.OnClickListener{

    public static final String TAG = "SalutTestApp";
    public SalutDataReceiver dataReceiver;
    public SalutServiceData serviceData;
    public Salut network;
    public Button hostingBtn;
    public Button discoverBtn;
    public Button sendMsgBtn;
    public ImageView streamImg;
    public TextView msg;
    SalutDataCallback callback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        hostingBtn = (Button) findViewById(R.id.hosting_button);
        discoverBtn = (Button) findViewById(R.id.discover_services);

        sendMsgBtn = (Button) findViewById(R.id.btn_send_msg);
        msg=(TextView)findViewById(R.id.textView);
        streamImg=(ImageView)findViewById(R.id.iv_streamImage);

        hostingBtn.setOnClickListener(this);
        discoverBtn.setOnClickListener(this);
        sendMsgBtn.setOnClickListener(this);

        /*Create a data receiver object that will bind the callback
        with some instantiated object from our app. */
        dataReceiver = new SalutDataReceiver(this, this);


        /*Populate the details for our awesome service. */
        serviceData = new SalutServiceData("wifiservice", 13334,"P2P");

        /*Create an instance of the Salut class, with all of the necessary data from before.
        * We'll also provide a callback just in case a device doesn't support WiFi Direct, which
        * Salut will tell us about before we start trying to use methods.*/
        network = new Salut(dataReceiver, serviceData, new SalutCallback() {
            @Override
            public void call() {
                // wiFiFailureDiag.show();
                // OR
                Log.e(TAG, "Sorry, but this device does not support WiFi Direct.");
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_items, menu);
        return true;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(network != null) {
            if (network.isRunningAsHost) {
                network.stopNetworkService(false);

            } else if(network.isDiscovering) {
                network.stopServiceDiscovery(false);
            }
        }
    }
    protected void stopHost() {

    }
    private void setupNetwork()
    {
        if(!network.isRunningAsHost)
        {
            network.startNetworkService(new SalutDeviceCallback() {
                @Override
                public void call(SalutDevice salutDevice) {
                    Toast.makeText(getApplicationContext(), "Device: " + salutDevice.instanceName + " connected.", Toast.LENGTH_SHORT).show();
                }
            });

            hostingBtn.setText("Stop Service");
            discoverBtn.setAlpha(0.5f);
            discoverBtn.setClickable(false);
        }
        else
        {
            network.stopNetworkService(false);
            hostingBtn.setText("Start Service");
            discoverBtn.setAlpha(1f);
            discoverBtn.setClickable(true);
        }
    }

    private void discoverServices()
    {
        if(!network.isRunningAsHost && !network.isDiscovering)
        {
            network.discoverNetworkServices(new SalutDeviceCallback() {
                @Override
                public void call(SalutDevice device) {
                    network.registerWithHost(device, new SalutCallback() {
                        @Override
                        public void call() {
                            Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                        }
                    }, new SalutCallback() {
                        @Override
                        public void call() {
                            Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }, true);

            discoverBtn.setText("Stop Discovery");
            hostingBtn.setAlpha(0.5f);
            hostingBtn.setClickable(false);
        }
        else
        {
            network.stopServiceDiscovery(true);
            discoverBtn.setText("Discover Services");
            hostingBtn.setAlpha(1f);
            hostingBtn.setClickable(false);
        }
    }

    private void sendMsg(){
        ResultResponse myResultResponse = new ResultResponse();
        String filePath= Helper.getRootDirectoryPath()+ "/DCIM/DirtHit/";
        File[] imgFile = new File(filePath).listFiles();
        if(imgFile != null && imgFile.length != 0 && imgFile[0].exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile[0].getAbsolutePath());
            myResultResponse.imgBlob=Helper.bitmapToString(myBitmap);
            myResultResponse.description = "contains image string";
        }
        else {
            myResultResponse.description="file not exist!! please check file path & image name!!!";
        }



        network.sendToAllDevices(myResultResponse, new SalutCallback() {
            @Override
            public void call() {
                Log.e(TAG, "Oh no! The data failed to send.");
            }
        });

        /*
        network.sendToHost(myResultResponse, new SalutCallback() {
            @Override
            public void call() {
                Log.e(TAG, "Oh no! The data failed to send.");
            }
        });*/
    }

    @Override
    public void onClick(View v) {

        if(!Salut.isWiFiEnabled(getApplicationContext()))
        {
            Toast.makeText(getApplicationContext(), "Please enable WiFi first.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(v.getId() == R.id.hosting_button)
        {
            setupNetwork();
        }
        else if(v.getId() == R.id.discover_services)
        {
            discoverServices();
        }
        else if(v.getId() == R.id.btn_send_msg)
        {
            sendMsg();
        }

    }

    @Override
    public void onDataReceived(Object data) {
        Toast.makeText(getApplicationContext(),"received",Toast.LENGTH_SHORT).show();
        try
        {
            ResultResponse newResultResponse = LoganSquare.parse(String.valueOf(data), ResultResponse.class);
            if( newResultResponse.imgBlob != null && newResultResponse.imgBlob != ""){
                Bitmap streamImgBitmap=Helper.stringToBitmap(newResultResponse.imgBlob);
                streamImg.setImageBitmap(streamImgBitmap);
            }
            else
            {

            }
            Log.d(TAG, newResultResponse.description);  //See you on the other side!
            //Do other stuff with data.
            msg.setText(newResultResponse.description);
        }
        catch (IOException ex)
        {
            Log.e(TAG, "Failed to parse network data.");
        }
    }
}
