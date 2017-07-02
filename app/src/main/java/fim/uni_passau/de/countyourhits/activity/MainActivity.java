package fim.uni_passau.de.countyourhits.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.kyleduo.switchbutton.SwitchButton;

import fim.uni_passau.de.countyourhits.R;

public class MainActivity extends AppCompatActivity {

    SwitchButton sbHostPlayer;
    Button btnStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void btnCameraClick(View view) {

    }

    public void btnConnectionClick(View view) {

    }
}
