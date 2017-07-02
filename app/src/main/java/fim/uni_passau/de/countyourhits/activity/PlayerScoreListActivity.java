package fim.uni_passau.de.countyourhits.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import fim.uni_passau.de.countyourhits.R;

public class PlayerScoreListActivity extends AppCompatActivity {
    public int getPlayerId;
    public Long getPlayerRequestId;
    public static final String PREFS = "playerPref";
    private SharedPreferences playerPreference;

    private ImageView img;
    private TextView name, score;
    public String playerImage, playerName, playerScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_score_list);

        //
        img = (ImageView)findViewById(R.id.playerScoreBoard);
        name = (TextView)findViewById(R.id.playerName);
        score = (TextView)findViewById(R.id.playerScore);


        playerPreference = getSharedPreferences(PREFS, 0);
        filterPlayerDataByPlayerId();
        Toast.makeText(PlayerScoreListActivity.this, "player id: " + getPlayerId + " ,requestId: " + getPlayerRequestId, Toast.LENGTH_SHORT).show();
    }

    private void filterPlayerDataByPlayerId() {

        Bundle getPlayerData = getIntent().getExtras();
        getPlayerId = getPlayerData.getInt("playerId");
        getPlayerRequestId = getPlayerData.getLong("requestId");

        if(getPlayerId == 101) {
            playerImage = "player_one_image";
            playerName = "player_one_name";
            playerScore = "player_one_score";
            setDataInListView(playerImage, playerName, playerScore);
        } else if (getPlayerId == 102) {
            playerImage = "player_two_image";
            playerName = "player_two_name";
            playerScore = "player_two_score";
            setDataInListView(playerImage, playerName, playerScore);
        } else if(getPlayerId == 103) {
            playerImage = "player_three_image";
            playerName = "player_three_name";
            playerScore = "player_three_score";
            setDataInListView(playerImage, playerName, playerScore);
        }
    }

    private void setDataInListView(String  playerImage, String playerName, String playerScore) {
        String showImage = playerPreference.getString(playerImage, null);
        String showName = playerPreference.getString(playerName, "Not found name");
        String showScore = playerPreference.getString(playerScore, "Not found score");
        img.setImageResource(getResources().getIdentifier(showImage, "drawable", getPackageName()));
        name.setText(showName);
        score.setText(showScore);


    }
}
