package fim.uni_passau.de.countyourhits.activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.peak.salut.Salut;

import java.util.List;

import fim.uni_passau.de.countyourhits.R;
import fim.uni_passau.de.countyourhits.adapter.PlayerAdapter;
import fim.uni_passau.de.countyourhits.database.PlayersDataSource;
import fim.uni_passau.de.countyourhits.database.ScoreDataSource;
import fim.uni_passau.de.countyourhits.model.Players;
import fim.uni_passau.de.countyourhits.model.Scores;

public class PlayerlistActivity extends AppCompatActivity {
    private static final String LOGTAG = "Dart_PlayerList";
    private List<Players> players;
    PlayersDataSource playersDataSource;

    //private static final String LOGTAG = "DartDB_ResultActivity";
    ScoreDataSource scoreDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playerlist);

        playersDataSource = new PlayersDataSource(this);
        playersDataSource.open();

        scoreDataSource = new ScoreDataSource(this);
        scoreDataSource.open();
        List<Scores> scoresData = scoreDataSource.findAll();

        for (Scores scores : scoresData) {
            String log = "Id: " + scores.getScoreId() + " ,Player Id: " + scores.getScorePlayer_Id() +
                    ", Score request no: " + scores.getScoreRequestNo() + ", score point: " + scores.getScorePoint() +
                    ", co-ordinate X: " + scores.getScoreCo_ordinate_x() + ", coordinate Y: " + scores.getScoreCo_ordinate_y() +
                    ", image path: " + scores.getScoreImageBlob() + " , date time: " + scores.getScoreDateTime() +
                    ", score note: " + scores.getScoreNote();
            Log.i("Name: ", log);
        }

        if (scoresData.size() < 4) {
            createDataForScores();
            Log.d("data created ", "dataplayer");
        }

        players = playersDataSource.findAll();
        final List<Players> playersData = (List<Players>) playersDataSource.findAll();

        PlayerAdapter customAdaptor = new PlayerAdapter(this, playersData);
        ListView customListView = (ListView) findViewById(R.id.playerlist);
        customListView.setAdapter(customAdaptor);

        customListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (checkNetworkConnection() == true) {
                    Players players = playersData.get(position);
                    Snackbar.make(view, players.getPlayerName() + "\n" + players.getPlayerId(), Snackbar.LENGTH_LONG)
                            .setAction("No action", null).show();
                    long randRequest = (long) Math.floor(Math.random() * 9_000_000L) + 1_000_000L;

                    Intent sendToResult = new Intent(getApplicationContext(), ResultActivity.class);
                    sendToResult.putExtra("playerId", players.getPlayerId());
                    sendToResult.putExtra("requestId", randRequest);
                    startActivity(sendToResult);
                } else {
                    Toast.makeText(getApplicationContext(), "Please check wifi connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        String[] playerName = new String[playersData.size()];
//        int index = 0;
//            for (Players value : playersData) {
//            playerName[index] = (String) value.getPlayerName();
//            index++;
//        }
//
//        ArrayAdapter adapter = new ArrayAdapter<String>(this,
//                 R.layout.item_row_players,R.id.player_name, playerName);
//
//        ListView listView = (ListView) findViewById(R.id.playerlist);
//        listView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        scoreDataSource.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scoreDataSource.close();
    }


    public void openDiscoverService() {
    //1. first check the wifi/network connection#
        // 2. open discover services
        //3. send connection request with playerid and request number(8digit random number
        long radnRequest = (long) Math.floor(Math.random() * 9_000_000L) + 1_000_000L;


    }

    public boolean checkNetworkConnection() {
        if (!Salut.isWiFiEnabled(getApplicationContext())){
            return false;
        } else {
            return true;
        }
    }

    public void createDataForScores() {
        Scores score = new Scores();
        score.setScorePlayer_Id(1501);
        score.setScoreRequestNo(45484);
        score.setScorePoint("score point of nahid 12");
        score.setScoreCo_ordinate_x("65.56");
        score.setScoreCo_ordinate_y("56.65");
        score.setScoreImageBlob("image path");
        score.setScoreDateTime("date time");
        score.setScoreNote("score note");
        score = scoreDataSource.create(score);
        Log.i(LOGTAG, "Score of  Nahid is with id" + score.getScoreId());

        score = new Scores();
        score.setScorePlayer_Id(1501);
        score.setScoreRequestNo(45484);
        score.setScorePoint("score point nahid 13");
        score.setScoreCo_ordinate_x("60.65");
        score.setScoreCo_ordinate_y("65.60");
        score.setScoreImageBlob("image path");
        score.setScoreDateTime("date time");
        score.setScoreNote("score note");
        score = scoreDataSource.create(score);
        Log.i(LOGTAG, "Score of  Nahid is with id" + score.getScoreId());

        score = new Scores();
        score.setScorePlayer_Id(1502);
        score.setScoreRequestNo(45484);
        score.setScorePoint("score point zia 10");
        score.setScoreCo_ordinate_x("65.56");
        score.setScoreCo_ordinate_y("65.56");
        score.setScoreImageBlob("image path");
        score.setScoreDateTime("date time");
        score.setScoreNote("score note");
        score = scoreDataSource.create(score);
        Log.i(LOGTAG, "Score of  Zia is with id" + score.getScoreId());

        score = new Scores();
        score.setScorePlayer_Id(1503);
        score.setScoreRequestNo(45484);
        score.setScorePoint("score point roji 12");
        score.setScoreCo_ordinate_x("65.56");
        score.setScoreCo_ordinate_y("65.56");
        score.setScoreImageBlob("image path");
        score.setScoreDateTime("date time");
        score.setScoreNote("score note");
        score = scoreDataSource.create(score);
        Log.i(LOGTAG, "Score of  Roji is with id" + score.getScoreId());
    }


}
