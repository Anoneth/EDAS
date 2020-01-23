package com.example.edas.activity;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import com.example.edas.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

public class AlarmReceiverActivity extends AppCompatActivity {

    MediaPlayer player;
    TextView textViewTitle;
    TextView textViewDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_receiver);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button buttonCloseAlarm = findViewById(R.id.buttonCloseAlarm);
        buttonCloseAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.stop();
                finish();
            }
        });

        textViewTitle = findViewById(R.id.textViewTitle);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewTitle.setText(getIntent().getExtras().getString("title"));
        textViewDescription.setText(getIntent().getExtras().getString("description"));

        startAlarm();
    }

    public void startAlarm() {
        player = new MediaPlayer();
        try {
            player.setDataSource(this, getAlarmFile());
            player.prepare();
            player.start();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    player.start();
                }
            });
        } catch (Exception ex) {
            Log.i("startAlarm", ex.getMessage());
        }
    }

    public Uri getAlarmFile() {
        String path = getExternalCacheDir() + "/" + getIntent().getExtras().getLong("id") + ".3gp";
        File file = new File(path);
        if (!file.exists()) {
            path = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).getEncodedPath();
            if (path == null) {
                path = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).getPath();
                if (path == null) {
                    path = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE).getPath();
                }
            }
        }
        Log.i("getAlarmFile", path);
        return Uri.parse(path);
    }

}
