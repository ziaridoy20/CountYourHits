package fim.uni_passau.de.countyourhits.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import fim.uni_passau.de.countyourhits.R;


public class PlayerlistActivity extends AppCompatActivity {
    String[] PlayersList = {"Roji","Nahid","Zia","Subash"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playerlist);

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                 R.layout.item_row_players,R.id.player_name, PlayersList);

        ListView listView = (ListView) findViewById(R.id.playerlist);
        listView.setAdapter(adapter);
    }
}
