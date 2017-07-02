package fim.uni_passau.de.countyourhits.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import com.peak.salut.Callbacks.SalutCallback;
import com.peak.salut.Callbacks.SalutDeviceCallback;
import com.peak.salut.Salut;
import com.peak.salut.SalutDevice;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

import fim.uni_passau.de.countyourhits.R;
import fim.uni_passau.de.countyourhits.database.DartDataSource;
import fim.uni_passau.de.countyourhits.model.Players;

public class PlayerActivity extends AppCompatActivity {

    public Salut network;
    public RadioGroup radioGroupPlayer;
    public RadioButton playerOne, playerTwo, playerThree;
    private Button playerChooseBtn;
    public static final String TAG = "PlayerChoose";
    public static final String PREFS = "playerPref";
    public static final String LOGTAG= "DBPlayer";
    private SharedPreferences playerPreference;

    private TextView tv;
    private Button btn;

    DartDataSource dataSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_player);
        setContentView(R.layout.activity_player);
        //for back navigation button in application
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        radioGroupPlayer = (RadioGroup) findViewById(R.id.radioGroupPlayer);
        playerOne = (RadioButton) findViewById(R.id.rbtnPlayerOne);
        playerTwo = (RadioButton) findViewById(R.id.rbtnPlayerTwo);
        playerThree = (RadioButton) findViewById(R.id.rbtnPlayerThree);
        playerChooseBtn = (Button) findViewById(R.id.btnPlayerChoose);
        radioGroupPlayer.clearCheck();

        tv = (TextView) findViewById(R.id.textView2);
        btn = (Button) findViewById(R.id.button);
        playerPreference = getSharedPreferences(PREFS, 0);
        storeSharedPreferenceData();

        dataSource = new DartDataSource(this);
        dataSource.open();

        List<Players> players = dataSource.findAll();
        if (players.size() == 0) {
            createData();
            players = dataSource.findAll();
        }

        for (Players data : players) {
            String log = "Id: " + data.getId() + " ,Name: " + data.getPlayerName() + " Score: " + data.getScore()
                    + " image:" + data.getScoreImage() + " desc " + data.getDescription();
            // Writing Contacts to log
            Log.d("Name: ", log);
        }

//        ArrayAdapter<Players> adapter = new ArrayAdapter<Players>(this,
//                android.R.layout.simple_list_item_1, players);
//        setListAdapter(adapter);

        playerChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRadiotButtonWithPlayerIdRequestId();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataSource.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
    }

    //for creating data
    private void createData() {
        Players players = new Players();
        players.setPlayerName("Nahid");
        players.setScore("score data 1121");
        players.setScoreImage("Image");
        players.setDescription("data description");
        players = dataSource.create(players);
        Log.i(LOGTAG, "data created with id" + players.getId());

        players = new Players();
        players.setPlayerName("Zia");
        players.setScore("Zia score 5454");
        players.setScoreImage("zia image");
        players.setDescription("Zia data description");
        players = dataSource.create(players);
        Log.i(LOGTAG, "Data created with id" + players.getId());
    }

    //for back button on phone
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "Service stop", Toast.LENGTH_SHORT).show();
    }

    //for back navigation button in application
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void display(View v) {
        String prefValue = playerPreference.getString("player_one_image", "Not found");
        tv.setText(prefValue);
    }
    public void storeSharedPreferenceData() {
        SharedPreferences.Editor playerEditor = playerPreference.edit();
        playerEditor.clear();
        //storing default data
        //playerOne with id 101
        playerEditor.putInt("player_one_id", 101); // Storing integer
        playerEditor.putString("player_one_name", "Player One"); // Storing string
        playerEditor.putString("player_one_image", "ic_player_one_socre_list_img"); // Storing integer
        playerEditor.putString("player_one_score", "Player Score Result: 100"); // Storing float

        playerEditor.putInt("player_one_id", 101); // Storing integer
        playerEditor.putString("player_one_name", "Player One1"); // Storing string
        playerEditor.putString("player_one_image", "ic_player_one_socre_list_img"); // Storing integer
        playerEditor.putString("player_one_score", "Player Score Result: 101"); // Storing float

        playerEditor.putInt("player_one_id", 101); // Storing integer
        playerEditor.putString("player_one_name", "Player One2"); // Storing string
        playerEditor.putString("player_one_image", "ic_player_one_socre_list_img"); // Storing integer
        playerEditor.putString("player_one_score", "Player Score Result: 102"); // Storing float

        playerEditor.putInt("player_one_id", 101); // Storing integer
        playerEditor.putString("player_one_name", "Player One3"); // Storing string
        playerEditor.putString("player_one_image", "ic_player_one_socre_list_img"); // Storing integer
        playerEditor.putString("player_one_score", "Player Score Result: 103"); // Storing float

        playerEditor.putInt("player_one_id", 101); // Storing integer
        playerEditor.putString("player_one_name", "Player One4"); // Storing string
        playerEditor.putString("player_one_image", "ic_player_one_socre_list_img"); // Storing integer
        playerEditor.putString("player_one_score", "Player Score Result: 104"); // Storing float


        //playerTwo with id 102
        playerEditor.putInt("player_two_id", 102); // Storing integer
        playerEditor.putString("player_two_name", "Player Two"); // Storing string
        playerEditor.putString("player_two_image", "ic_player_two_socre_list_img"); // Storing integer
        playerEditor.putString("player_two_score", "Player Score Result: 202"); // Storing float

        //playerThreee
        playerEditor.putInt("player_three_id", 103); // Storing integer
        playerEditor.putString("player_three_name", "Player Three"); // Storing string
        playerEditor.putString("player_three_image", "ic_player_three_socre_list_img"); // Storing integer
        playerEditor.putString("player_three_score", "Player Score Result: 303"); // Storing float
        playerEditor.commit(); // commit changes
    }

    private void checkRadiotButtonWithPlayerIdRequestId() {
        RadioButton rb = (RadioButton) radioGroupPlayer.findViewById(radioGroupPlayer.getCheckedRadioButtonId());

        if(rb == null) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlayerActivity.this);
            alertDialog.setTitle("Select a player")
                    .setMessage("Please select any player")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = alertDialog.create();
            alert.show();
        } else  {
            //Toast.makeText(PlayerActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();


            long number = (long) Math.floor(Math.random() * 9_000_000L) + 1_000_000L;
            int playerOneId = 101;
            int playerTwoId = 102;
            int playerThreeId = 103;
            // Check which radio button was clicked
            switch(rb.getId()) {
                case R.id.rbtnPlayerOne:
                    sendPlayerRequest(playerOneId, number);
                    break;
                case R.id.rbtnPlayerTwo:
                    sendPlayerRequest(playerTwoId, number);
                    break;
                case R.id.rbtnPlayerThree:
                    sendPlayerRequest(playerThreeId, number);
                    break;
                default:
                    Log.e(TAG, "no case");
                    Toast.makeText(PlayerActivity.this, "no matching case", Toast.LENGTH_SHORT).show();
                    return;
            }
        }
    }

    private boolean checkConnection() {

        return true;
    }

    private void sendPlayerRequest(int playerId, long requestId) {
        if(checkConnection() == true) {

            Intent retrievePalyerData = new Intent(getApplicationContext(), PlayerScoreListActivity.class);
            retrievePalyerData.putExtra("playerId", playerId);
            retrievePalyerData.putExtra("requestId", requestId);
            startActivity(retrievePalyerData);

        } else {
            Toast.makeText(PlayerActivity.this, "please make connection before proceed", Toast.LENGTH_SHORT).show();
        }

    }

//    private void checkingHostConnection() {
//        if(!network.isRunningAsHost && !network.isDiscovering) {
//            network.discoverNetworkServices(new SalutDeviceCallback() {
//                @Override
//                public void call(final SalutDevice device) {
//                    network.registerWithHost(device, new SalutCallback() {
//                        @Override
//                        public void call() {
//                            Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
//                        }
//                    }, new SalutCallback() {
//                        @Override
//                        public void call() {
//
//                            //Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            }, true);
//        }
//        else {
//
//        }
//    }


}
