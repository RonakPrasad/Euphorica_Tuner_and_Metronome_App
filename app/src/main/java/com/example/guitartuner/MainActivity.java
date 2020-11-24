package com.example.guitartuner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import static android.Manifest.permission.RECORD_AUDIO;

public class MainActivity extends AppCompatActivity {

    final int RequestPermissionCOde = 1;

    FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
    final float FREQUENCY_RESOLUTION = 4.f;
    ImageView tickerImageView;
    TextView DominantFrequency;
    Button rotateButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);

        tickerImageView = findViewById(R.id.tickerImageView);
        DominantFrequency = findViewById(R.id.DominantFreqText);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //set tuner selected
        bottomNavigationView.setSelectedItemId(R.id.tuner);

        //perform itemselectedlistener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.metronome:
                        startActivity(new Intent(getApplicationContext(),MetronomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.tuner:
                        return true;
                }
                return false;
            }
        });
}

    // Will request microphone permission or start analyzing audio on window gain focus
    // Stops the audio thread if it exists on lose focus
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if(hasFocus)
        {
            if(checkPermissions()){
                if(frequencyAnalyzer.mAudioPoll == null){
                    frequencyAnalyzer.setFrequencyResolution(FREQUENCY_RESOLUTION);
                    frequencyAnalyzer.createPollAudioThread(DominantFrequency, tickerImageView);
                    frequencyAnalyzer.runPollAudio();
                }
            } else{
                requestPermission();
            }
        }
        else{
            if(frequencyAnalyzer.mAudioPoll != null){
                frequencyAnalyzer.stopPollAudio();
            }
        }
    }

    /********
     * Permissions functions
     *******/
    private void requestPermission(){
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO},
                RequestPermissionCOde);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode){
            case RequestPermissionCOde:
                if (grantResults.length > 0) {

                    boolean RecordPermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;

                    if(RecordPermission){
                        Toast.makeText(MainActivity.this, "Permission granted",
                                Toast.LENGTH_LONG).show();

                    }
                    else{
                        Toast.makeText(MainActivity.this, "Permission denied",
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermissions(){
        return ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }
}
