package fim.uni_passau.de.countyourhits.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import fim.uni_passau.de.countyourhits.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btnCameraClick(View view) {
        Intent cameraIntent = new Intent(this, ColorBlobDetectionActivity.class);
        startActivity(cameraIntent);
    }

    public void btnConnectionClick(View view) {
        Intent connectionIntent = new Intent(this, DartWiFiDirectActivity.class);
        startActivity(connectionIntent);
    }
}
