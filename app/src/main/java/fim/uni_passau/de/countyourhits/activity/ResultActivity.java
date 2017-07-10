package fim.uni_passau.de.countyourhits.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.peak.salut.Callbacks.SalutCallback;
import com.peak.salut.Callbacks.SalutDataCallback;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutDevice;
import com.peak.salut.SalutServiceData;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.Orientation;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.io.IOException;
import java.util.List;

import fim.uni_passau.de.countyourhits.R;
import fim.uni_passau.de.countyourhits.adapter.ResultAdapter;
import fim.uni_passau.de.countyourhits.app.Helper;
import fim.uni_passau.de.countyourhits.database.ScoreDataSource;
import fim.uni_passau.de.countyourhits.model.Players;
import fim.uni_passau.de.countyourhits.model.Scores;
import fim.uni_passau.de.countyourhits.model.ScoresMsg;

public class ResultActivity extends AppCompatActivity implements DiscreteScrollView.OnItemChangedListener,
        View.OnClickListener, SalutDataCallback {
    private List<Scores> resultList;

    private TextView raScorePoint;
    private TextView raPlayerId;
    private TextView raCenterPoint;
    private ImageView rateItemButton;
    private DiscreteScrollView itemPicker;
    private InfiniteScrollAdapter infiniteAdapter;
    Players players;
    private List<Scores> scores;
    //
    //salut p2p connection
    //
    public static final String TAG = "Salut";
    public SalutDataReceiver dataReceiver;
    public SalutServiceData serviceData;
    public Salut network;
    SalutDataCallback callback;
    ProgressBar progressBar;
    CardView image;
    SharedPreferences playerPreference;

    private long requestId,playerId;


    private static final String LOGTAG = "DartDB_ResultActivity";
    ScoreDataSource scoreDataSource;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        scoreDataSource = new ScoreDataSource(this);


//        List<Scores> scoresData = scoreDataSource.findAll();
//        for (Scores scores : scoresData) {
//            String log = "Id: " + scores.getScoreId() + " ,Player Id: " + scores.getScorePlayer_Id() +
//                    ", Score request no: " + scores.getScoreRequestNo() + ", score point: " + scores.getScorePoint() +
//                    ", co-ordinate X: " + scores.getScoreCo_ordinate_x() + ", coordinate Y: " + scores.getScoreCo_ordinate_y() +
//                    ", image path: " + scores.getScoreImageBlob() + " , date time: " + scores.getScoreDateTime() +
//                    ", score note: " + scores.getScoreNote();
//            Log.d("Name: ", log);
//        }
        Bundle getPlayerDataByIntent = getIntent().getExtras();
        requestId = getPlayerDataByIntent.getLong("requestId");
        playerId = getPlayerDataByIntent.getLong("playerId");
//        playerPreference = getSharedPreferences("PREFS", 0);
//        long playerId = playerPreference.getLong("player_id", 0);
//        long requestId = playerPreference.getLong("request_id",0);
//        Toast.makeText(getApplicationContext(), playerId + " " + requestId, Toast.LENGTH_LONG).show();


        raScorePoint = (TextView) findViewById(R.id.item_score_point);
        raPlayerId = (TextView) findViewById(R.id.item_player_id);
        raCenterPoint= (TextView) findViewById(R.id.item_center_point);
        //rateItemButton = (ImageView) findViewById(R.id.item_btn_rate);

        //shop = Shop.get();
        initSalut();
        if(resultList != null){
            resultList.clear();
        }
        resultList = getData(playerId,requestId);
        itemPicker = (DiscreteScrollView) findViewById(R.id.item_picker);
        itemPicker.setOrientation(Orientation.HORIZONTAL);
        itemPicker.addOnItemChangedListener(this);
        infiniteAdapter = InfiniteScrollAdapter.wrap(new ResultAdapter(resultList));
        itemPicker.setAdapter(infiniteAdapter);
        itemPicker.setItemTransitionTimeMillis(1000);
        itemPicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

        onItemChanged(resultList.get(0));


        //findViewById(R.id.item_btn_rate).setOnClickListener(this);
        //findViewById(R.id.item_btn_buy).setOnClickListener(this);
        //findViewById(R.id.item_btn_comment).setOnClickListener(this);

        //findViewById(R.id.home).setOnClickListener(this);
        //findViewById(R.id.btn_smooth_scroll).setOnClickListener(this);
        //findViewById(R.id.btn_transition_time).setOnClickListener(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (network != null) {
            network.stopServiceDiscovery(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.item_btn_rate:
                ResultResponse current = resultList.get(itemPicker.getCurrentItem());
                //shop.setRated(current.getId(), !shop.isRated(current.getId()));
                changeRateButtonState(current);
                break;*/
            case R.id.home:
                finish();
                break;
           /* case R.id.btn_transition_time:
                //DiscreteScrollViewOptions.configureTransitionTime(itemPicker);
                break;
            case R.id.btn_smooth_scroll:
                //DiscreteScrollViewOptions.smoothScrollToUserSelectedPosition(itemPicker, v);
                break;*/
            default:
                showUnsupportedSnackBar();
                break;
        }
    }

    private void onItemChanged(Scores item) {
        raScorePoint.setText(item.getScorePoint());
        raPlayerId.setText(String.valueOf(item.getScorePlayer_Id()));
        raCenterPoint.setText(item.getScoreCo_ordinate_x()+ ","+item.getScoreCo_ordinate_y());
        //changeRateButtonState(item);
    }

    private void changeRateButtonState(Scores item) {

    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int position) {
        int positionInDataSet = infiniteAdapter.getRealPosition(position);
        onItemChanged(resultList.get(positionInDataSet));
    }

    private void showUnsupportedSnackBar() {
        Snackbar.make(itemPicker, "Unsuccessful", Snackbar.LENGTH_SHORT).show();
    }


    public List<Scores> getData(long playerId, long requestId) {
        scoreDataSource.open();
       List<Scores> playerScores = scoreDataSource.findByPlayerId("fk_player_id == "+playerId, "score_id DESC");

        return playerScores;
    }

    public void initSalut() {
        progressBar = (ProgressBar) findViewById(R.id.pbar_result);
        progressBar.setVisibility(View.GONE);
        image = (CardView) findViewById(R.id.card_view);

        dataReceiver = new SalutDataReceiver(this, this);
        serviceData = new SalutServiceData("wifiservice", 13334, "P2P");
        network = new Salut(dataReceiver, serviceData, new SalutCallback() {
            @Override
            public void call() {
                Log.e(TAG, "Sorry, but this device does not support WiFi Direct.");
            }
        });
        discoverServices();
    }

    private void discoverServices() {
        progressBar.setVisibility(View.VISIBLE);
        image.setVisibility(View.GONE);

        if (!network.isRunningAsHost && !network.isDiscovering) {
            //
            //comitted by roji
            //
            network.discoverWithTimeout(new SalutCallback() {
                @Override
                public void call() {

                    final SalutDevice device = network.foundDevices.get(0);
                    network.registerWithHost(device, new SalutCallback() {
                        @Override
                        public void call() {
                            progressBar.setVisibility(View.GONE);
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ResultActivity.this);
                            alertDialog.setTitle("Client Device Connected")
                                    .setMessage("Device: " + device.deviceName + " connected as host")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = alertDialog.create();
                            alert.show();
                            Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                            image.setVisibility(View.VISIBLE);
                        }
                    }, new SalutCallback() {
                        @Override
                        public void call() {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ResultActivity.this);
                            alertDialog.setTitle("Disconnected")
                                    .setMessage("Network Disconnected, Try again?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            initSalut();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent playerlistIntent = new Intent(getApplicationContext(), PlayerlistActivity.class);
                                            startActivity(playerlistIntent);

                                        }
                                    });
                            AlertDialog alert = alertDialog.create();
                            alert.show();
                        }
                    });
                }

            }, new SalutCallback() {
                @Override
                public void call() {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ResultActivity.this);
                    alertDialog.setTitle("Unsuccessful")
                            .setMessage("Sorry Unable to Connect, Try again?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    initSalut();

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent playerlistIntent = new Intent(getApplicationContext(), PlayerlistActivity.class);
                                    startActivity(playerlistIntent);
                                }
                            });

                    AlertDialog alert = alertDialog.create();
                    alert.show();
                }

            }, 10000);

        } else {
            stopDiscovery();
        }
    }


    protected void stopDiscovery() {
        network.stopServiceDiscovery(true);
    }

    //for back button on phone
//     @Override
//     public void onBackPressed() {
//        super.onBackPressed();
//        Toast.makeText(this, "Discover Service Stopped", Toast.LENGTH_SHORT).show();
//        stopDiscovery();
//
//     }
//      //for back navigation button in application
//      @Override
//     public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                Toast.makeText(this, "Discover Service Stopped", Toast.LENGTH_SHORT).show();
//                stopDiscovery();
//                finish();
//                return true;
//            }
//        return super.onOptionsItemSelected(item);
//      }

    @Override
    public void onDataReceived(Object data) {
        Toast.makeText(getApplicationContext(),"received",Toast.LENGTH_SHORT).show();
        try
        {

            ScoresMsg newScore = LoganSquare.parse(String.valueOf(data), ScoresMsg.class);
            if( newScore != null && newScore.imgBlob != ""){
                Bitmap saveImg= Helper.stringToBitmap(newScore.imgBlob);
                String filepath=Helper.storeImage(saveImg);
                Scores nwScore= new Scores();
                nwScore.setScorePlayer_Id(Long.valueOf(newScore.scorePlayer_Id));
                nwScore.setScoreNote("test");
                nwScore.setScoreImageBlob(filepath);
                nwScore.setScoreId(Long.valueOf(newScore.scoreId));
                nwScore.setScoreCo_ordinate_x(newScore.scoreCo_ordinate_x);
                nwScore.setScoreCo_ordinate_y(newScore.scoreCo_ordinate_y);
                nwScore.setScoreDateTime(newScore.scoreDateTime);
                nwScore.setScorePoint(newScore.scorePoint);
                nwScore.setScoreRequestNo(Long.valueOf(newScore.scoreRequestNo));

                resultList.clear();

                resultList.add(nwScore);
                //notifyAll();
                infiniteAdapter.notifyDataSetChanged();

            }
            else {

            }

        }
        catch (IOException ex)
        {
            Log.e(TAG, "Failed to parse network resultList.");
        }
    }


}
