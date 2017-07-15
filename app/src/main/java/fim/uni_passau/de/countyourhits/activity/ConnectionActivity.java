package fim.uni_passau.de.countyourhits.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import fim.uni_passau.de.countyourhits.R;
import fim.uni_passau.de.countyourhits.app.Helper;
import fim.uni_passau.de.countyourhits.model.Message;
import fim.uni_passau.de.countyourhits.model.ScoresMsg;

public class ConnectionActivity extends AppCompatActivity  implements SalutDataCallback, View.OnClickListener{

    public static final String TAG = "SalutTestApp";
    public SalutDataReceiver dataReceiver;
    public SalutServiceData serviceData;
    public Salut network;
    public Button hostingBtn;
    public Button discoverBtn;
    public Button sendMsgBtn;
    public Button selectPlayer;
    public ImageView streamImg;
    public TextView msg;
    SalutDataCallback callback;
    ProgressBar progressBar;
    public long playerId=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        progressBar = (ProgressBar) findViewById(R.id.pbar_result);
        progressBar.setVisibility(View.GONE);
        hostingBtn = (Button) findViewById(R.id.hosting_button);
        discoverBtn = (Button) findViewById(R.id.discover_services);
        selectPlayer = (Button) findViewById(R.id.btn_selectPlayer);
        sendMsgBtn = (Button) findViewById(R.id.btn_send_msg);
        msg=(TextView)findViewById(R.id.textView);
        streamImg=(ImageView)findViewById(R.id.iv_streamImage);

        hostingBtn.setOnClickListener(this);
        discoverBtn.setOnClickListener(this);
        sendMsgBtn.setOnClickListener(this);
        selectPlayer.setOnClickListener(this);
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConnectionActivity.this);
        alertDialog.setMessage("Do you want to stop Host Service ?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        network.stopNetworkService(true);
                        hostingBtn.setText("START SERVICE");
                        discoverBtn.setAlpha(1f);
                        discoverBtn.setClickable(true);
                        hostingBtn.setBackgroundResource(android.R.drawable.btn_default);
                        hostingBtn.setTextColor(Color.BLACK);

//                        //if host is disconnected remove the state
//                        network.stopServiceDiscovery(true);
//                        discoverBtn.setText("DISCOVERY SERVICE");
//                        hostingBtn.setAlpha(1f);
//                        hostingBtn.setClickable(false);
//                        progressBar.setVisibility(View.GONE);
//                        hostingBtn.setBackgroundResource(android.R.drawable.btn_default);
//                        discoverBtn.setTextColor(Color.BLACK);
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
    protected void stopDiscovery() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConnectionActivity.this);
        alertDialog.setMessage("Do you want to STOP DISCOVERY?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        network.stopServiceDiscovery(true);
                        discoverBtn.setText("DISCOVERY SERVICE");
                        hostingBtn.setAlpha(1f);
                        hostingBtn.setClickable(true);
                        progressBar.setVisibility(View.GONE);
                        hostingBtn.setBackgroundResource(android.R.drawable.btn_default);
                        discoverBtn.setTextColor(Color.BLACK);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialog.create();
        alertDialog.setTitle("Stop Discovery");
        alert.show();
    }
    private void setupNetwork()    {
        if(!network.isRunningAsHost)
        {
            network.startNetworkService(new SalutDeviceCallback() {
                @Override
                public void call(SalutDevice salutDevice) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConnectionActivity.this);
                    alertDialog.setTitle("Host Device Connected")
                            .setMessage("Device: "+ salutDevice.deviceName + " connected as client")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = alertDialog.create();
                    alertDialog.setTitle("STOP Discovery");
                    alert.show();
                    hostingBtn.setBackgroundColor(Color.BLUE);
                    hostingBtn.setTextColor(Color.WHITE);
                    Log.e(TAG, "Device: " + salutDevice.instanceName);
                    //Toast.makeText(getApplicationContext(), "Device: " + salutDevice.instanceName + " connected.", Toast.LENGTH_SHORT).show();
                }
            });

            hostingBtn.setText("STOP SERVICE");
            discoverBtn.setAlpha(0.5f);
            discoverBtn.setClickable(false);
        } else {
            stopHost();
//            old code
//            network.stopNetworkService(false);
//            hostingBtn.setText("Start Service");
//            discoverBtn.setAlpha(1f);
//            discoverBtn.setClickable(true);
        }
    }

    private void discoverServices() {
        progressBar.setVisibility(View.VISIBLE);
        if(!network.isRunningAsHost && !network.isDiscovering) {
            network.discoverNetworkServices(new SalutDeviceCallback() {
                @Override
                public void call(final SalutDevice device) {
                    network.registerWithHost(device, new SalutCallback() {
                        @Override
                        public void call() {
                            progressBar.setVisibility(View.GONE);
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConnectionActivity.this);
                            alertDialog.setTitle("Client Device Connected")
                                    .setMessage("Device: " + device.deviceName + " connected as host")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = alertDialog.create();
                            alertDialog.setTitle("STOP Discovery");
                            alert.show();
                            discoverBtn.setBackgroundColor(Color.BLUE);
                            discoverBtn.setTextColor(Color.WHITE);
                            //Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                        }
                    }, new SalutCallback() {
                        @Override
                        public void call() {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConnectionActivity.this);
                            alertDialog.setTitle("Unsuccessful")
                                    .setMessage("Sorry Unable to Connect, Try again")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            progressBar.setVisibility(View.GONE);
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = alertDialog.create();
                            alertDialog.setTitle("STAR DISCOVERY");
                            alert.show();
                            discoverBtn.setBackgroundResource(android.R.drawable.btn_default);
                            discoverBtn.setTextColor(Color.BLACK);
                            //Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }, true);

            discoverBtn.setText("STOP DISCOVERY");
            hostingBtn.setAlpha(0.5f);
            hostingBtn.setClickable(false);
        }
        else {
            stopDiscovery();
//            old code
//            network.stopServiceDiscovery(true);
//            discoverBtn.setText("Discover Services");
//            hostingBtn.setAlpha(1f);
//            hostingBtn.setClickable(false);
        }
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

        else if(v.getId() == R.id.btn_selectPlayer)
        {
            Intent playerlistIntent = new Intent(getApplicationContext(), PlayerlistActivity.class);
            startActivity(playerlistIntent);
        }

    }

    public void goToPlayerView(View vew) {
        //first check connected to host or not then apply
//        if(!network.isConnectedToAnotherDevice) {
//            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConnectionActivity.this);
//            alertDialog.setTitle("Please connected to host first")
//                    .setMessage("Your device is not connected")
//                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    });
//            AlertDialog alert = alertDialog.create();
//            alert.show();
//        } else {
//            Intent selectPlayerActivity = new Intent(getApplicationContext(), PlayerActivity.class);
//            startActivity(selectPlayerActivity);
//        }

        //just remove this part
        Intent selectPlayerActivity = new Intent(getApplicationContext(), ResultActivity.class);
        startActivity(selectPlayerActivity);
    }
    //    saving image to device
    private void storeImage(Bitmap image) {
        //save image
        OutputStream output;
        Date dateObj = new Date();
        CharSequence currentDateTime = DateFormat.format("yyyy-MM-dd hh:mm:ss", dateObj.getTime());

        File filepath = Environment.getExternalStorageDirectory();
        File dir = new File(filepath.getAbsolutePath() + "/Save_Image_Example");
        if(!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, currentDateTime + ".png");
//        Toast.makeText(ConnectionActivity.this, "Image Saved to SD Card", Toast.LENGTH_SHORT).show();

        try {
            output = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, output);
            output.flush();
            output.close();

        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDataReceived(Object data) {
        Toast.makeText(getApplicationContext(),"received",Toast.LENGTH_SHORT).show();
        try
        {
            Message newMessage = LoganSquare.parse(String.valueOf(data), Message.class);
            if( newMessage.imgBlob != null && newMessage.imgBlob != ""){
                Bitmap streamImgBitmap=Helper.stringToBitmap(newMessage.imgBlob);
                streamImg.setImageBitmap(streamImgBitmap);
                storeImage(streamImgBitmap);
            }
            else {

            }
            //Log.d(TAG, newMessage.description);  //See you on the other side!
            //Do other stuff with data.
            //msg.setText(newMessage.description);
            Log.i(TAG, String.valueOf(newMessage.playerId));
            msg.setText(String.valueOf(newMessage.playerId));
            playerId=newMessage.playerId;
        }
        catch (IOException ex)
        {
            Log.e(TAG, "Failed to parse network data.");
        }
    }

    private void sendMsg(){
        ScoresMsg myMessage = new ScoresMsg();
        String filePath= Helper.getRootDirectoryPath()+ "/DCIM/DirtHit/";
        File[] imgFile = new File(filePath).listFiles();
        Random rndNumber= new Random();

        if(imgFile != null && imgFile.length != 0 && imgFile[0].exists()){
            int fileNumber= rndNumber.nextInt(imgFile.length) - 1;
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile[fileNumber].getAbsolutePath());
            myMessage.imgBlob=Helper.bitmapToString(myBitmap);
            myMessage.scoreCo_ordinate_x="64.0";
            myMessage.scoreCo_ordinate_y="53.0";

            //myMessage.scoreDateTime="07/10/2017";
            myMessage.scoreDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).toString();
            //myMessage.scoreId="12";
            myMessage.scoreImagePath="";
            myMessage.scoreNote="test";
            myMessage.scorePlayer_Id = String.valueOf(playerId);
            myMessage.scorePoint=String.valueOf(rndNumber.nextInt());
            myMessage.scoreRequestNo =String.valueOf(rndNumber.nextInt());
        }
        else {
        }

        Toast.makeText(getApplicationContext(),String.valueOf(playerId),Toast.LENGTH_SHORT).show();

        network.sendToAllDevices(myMessage,  new SalutCallback() {
            @Override
            public void call() {
                Log.e(TAG, "Oh no! The data failed to send.");
            }
        });
        Toast.makeText(getApplicationContext(),"Data Sent", Toast.LENGTH_SHORT).show();
        /*
        network.sendToHost(myMessage, new SalutCallback() {
            @Override
            public void call() {
                Log.e(TAG, "Oh no! The data failed to send.");
            }
        });*/
    }
}