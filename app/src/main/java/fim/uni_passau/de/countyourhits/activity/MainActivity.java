package fim.uni_passau.de.countyourhits.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.kyleduo.switchbutton.SwitchButton;

import java.util.List;

import fim.uni_passau.de.countyourhits.R;
import fim.uni_passau.de.countyourhits.database.PlayersDataSource;
import fim.uni_passau.de.countyourhits.database.ScoreDataSource;
import fim.uni_passau.de.countyourhits.model.Players;
import fim.uni_passau.de.countyourhits.model.Scores;

public class MainActivity extends AppCompatActivity {

    SwitchButton sbHostPlayer;
    Button btnStart;
    private static final String LOGTAG = "DartDB_Main";
    PlayersDataSource playersDataSource;
//    ScoreDataSource scoreDataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playersDataSource = new PlayersDataSource(this);
        playersDataSource.open();

//        scoreDataSource = new ScoreDataSource(this);
//        scoreDataSource.open();

        List<Players> playersData = playersDataSource.findAll();
        if (playersData.size() < 4) {
                createDataForPlayers();

            Log.d("data created ", "dataplayer");
        }
//        List<Scores> scoresData = scoreDataSource.findAll();
//        if (scoresData.size() == 0 ) {
//            createDataForScores();
//            scoresData = scoreDataSource.findAll();
//        }
//        for (Players players : playersData) {
//            String log = "Id: " + players.getPlayerId() + " ,Name: " + players.getPlayerName() +
//                    " , player image path: " + players.getPlayerImage() +
//                    ", player note: " + players.getPlayerNote();
//            Log.d("Name: ", log);
//        }


//        for (Scores scores : scoresData) {
//            String log = "Id: " + scores.getScoreId() + " ,Player Id: " + scores.getScorePlayer_Id() +
//                    ", Score request no: " + scores.getScoreRequestNo() + ", score point: " + scores.getScorePoint() +
//                    ", co-ordinate X: " + scores.getScoreCo_ordinate_x() + ", coordinate Y: " + scores.getScoreCo_ordinate_y() +
//                    ", image path: " + scores.getScoreImagePath() + " , date time: " + scores.getScoreDateTime() +
//                    ", score note: " + scores.getScoreNote();
//            Log.d("Name: ", log);
//        }
        sbHostPlayer=(SwitchButton)findViewById(R.id.sb_host_player);
        btnStart=(Button)findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sbHostPlayer.isChecked()){
                    Intent cameraIntent = new Intent(getApplicationContext(), ColorBlobDetectionActivity.class);
                    startActivity(cameraIntent);
                }
                else{
                    Intent playerlistIntent = new Intent(getApplicationContext(), PlayerlistActivity.class);
                    startActivity(playerlistIntent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        playersDataSource.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        playersDataSource.close();
    }

    public void createDataForPlayers() {
        Players player = new Players();
        player.setPlayerId(1501);
        player.setPlayerName("Nahid");
        player.setPlayerImage("ic_player_nahid");
        player.setPlayerNote("Description about Nahid");
        player = playersDataSource.create(player);
        Log.i(LOGTAG, "Player Nahid is create with id" + player.getPlayerId());

        player = new Players();
        player.setPlayerId(1502);
        player.setPlayerName("Zia");
        player.setPlayerImage("ic_player_zia");
        player.setPlayerNote("Description about Zia");
        player = playersDataSource.create(player);
        Log.i(LOGTAG, "Player Zia is create with id" + player.getPlayerId());

        player = new Players();
        player.setPlayerId(1503);
        player.setPlayerName("Roji");
        player.setPlayerImage("ic_player_roji");
        player.setPlayerNote("Description about Roji");
        player = playersDataSource.create(player);
        Log.i(LOGTAG, "Player Roji is create with id" + player.getPlayerId());

        player = new Players();
        player.setPlayerId(1504);
        player.setPlayerName("Subash");
        player.setPlayerImage("ic_player_subash_ale");
        player.setPlayerNote("Description about Subash");
        player = playersDataSource.create(player);
        Log.i(LOGTAG, "Player Subash is create with id" + player.getPlayerId());

    }

//    public void createDataForScores() {
//        Scores score = new Scores();
//        score.setScorePlayer_Id(001);
//        score.setScoreRequestNo(45484);
//        score.setScorePoint("score point");
//        score.setScoreCo_ordinate_x("co ordinate x");
//        score.setScoreCo_ordinate_y("co ordinate y");
//        score.setScoreImagePath("image path");
//        score.setScoreDateTime("date time");
//        score.setScoreNote("score note");
//        score = scoreDataSource.create(score);
//        Log.i(LOGTAG, "Score of  Nahid is with id" + score.getScoreId());
//    }


}
