package fim.uni_passau.de.countyourhits.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent resultIntent = new Intent(getApplicationContext(), ResultActivity.class);
                startActivity(resultIntent);
            }
        });
    }

}