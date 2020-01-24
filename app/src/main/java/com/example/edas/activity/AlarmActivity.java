package com.example.edas.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;

import com.example.edas.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.provider.AlarmClock;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TimePicker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {

    private EditText editTextSetDate;
    private EditText editTextSetTime;
    private Button buttonCreateAlarm;
    private Button buttonRemoveAlarm;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private boolean fileExist = false;

    private boolean alarmExists = false;

    private long id;

    private ImageButton buttonRecord;
    private ImageButton buttonPlay;
    private ImageButton buttonRemoveFile;
    private boolean isRecording;
    private boolean isPlaying;

    private boolean recordPermissionAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                recordPermissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        editTextSetDate = findViewById(R.id.editTextSetDate);
        editTextSetTime = findViewById(R.id.editTextSetTime);

        id = getIntent().getExtras().getLong("id");

        editTextSetDate.setText(getIntent().getExtras().getString("date"));
        editTextSetTime.setText(getIntent().getExtras().getString("time"));

        final Calendar myCalendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            myCalendar.setTime(format.parse(getIntent().getExtras().getString("date") + " " + getIntent().getExtras().getString("time")));
        } catch (Exception ex) {
            Log.e("parseDate", ex.getMessage());
        }

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                editTextSetDate.setText(new SimpleDateFormat("yyyy-MM-dd", getResources().getConfiguration().locale).format(myCalendar.getTime()));
            }
        };

        editTextSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AlarmActivity.this, dateSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final TimePickerDialog timePickerDialog = new TimePickerDialog(AlarmActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                editTextSetTime.setText(String.format("%02d:%02d", hourOfDay, minute));
            }
        }, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);

        editTextSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.setTitle(getString(R.string.pick_time));
                timePickerDialog.show();
            }
        });

        fileName = getExternalCacheDir().getAbsolutePath() + "/" + id + ".3gp";

        buttonRecord = findViewById(R.id.buttonRecord);
        buttonPlay = findViewById(R.id.buttonPlay);
        buttonRemoveFile = findViewById(R.id.buttonRemoveFile);

        buttonRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) startRecord();
                else stopRecord();
            }
        });

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) startPlay();
                else stopPlay();
            }
        });

        buttonRemoveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileExist) {
                    File file = new File(fileName);
                    file.delete();
                    fileExist = false;
                    buttonRemoveFile.setEnabled(fileExist);
                    buttonPlay.setEnabled(fileExist);
                }
            }
        });

        File file = new File(fileName);
        fileExist = file.exists();
        buttonPlay.setEnabled(fileExist);
        buttonRemoveFile.setEnabled(fileExist);

        buttonCreateAlarm = findViewById(R.id.buttonCreateAlarm);
        buttonCreateAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    calendar.setTime(format.parse(editTextSetDate.getText().toString() + " " + editTextSetTime.getText().toString()));
                    Intent intent = new Intent(AlarmActivity.this, AlarmReceiverActivity.class);
                    intent.putExtra("id", getIntent().getExtras().getLong("id"));
                    intent.putExtra("title", getIntent().getExtras().getString("title"));
                    intent.putExtra("description", getIntent().getExtras().getString("description"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    PendingIntent pendingIntent = PendingIntent.getActivity(AlarmActivity.this, (int) id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager am = (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
                    am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    alarmExists = true;
                    buttonRemoveAlarm.setEnabled(alarmExists);
                } catch (Exception ex) {
                    Log.e("setAlarm", ex.getMessage());
                }
            }
        });

        Intent intent = new Intent(getApplicationContext(), AlarmReceiverActivity.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) id, intent, PendingIntent.FLAG_NO_CREATE);
        alarmExists = pendingIntent != null;

        buttonRemoveAlarm = findViewById(R.id.buttonRemoveAlarm);
        buttonRemoveAlarm.setEnabled(alarmExists);
        buttonRemoveAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager am = (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
                Intent intent = new Intent(getApplicationContext(), AlarmReceiverActivity.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                am.cancel(pendingIntent);
                alarmExists = false;
                buttonRemoveAlarm.setEnabled(false);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(getApplicationContext(), AlarmReceiverActivity.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) id, intent, PendingIntent.FLAG_NO_CREATE);
        alarmExists = pendingIntent != null;

        buttonRemoveAlarm.setEnabled(alarmExists);
    }

    private void startPlay() {
        if (isRecording) stopRecord();
        isPlaying = true;
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlay();
                }
            });
        } catch (Exception ex) {
            Log.i("startPlay", ex.getMessage());
        }
    }

    private void stopPlay() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        isPlaying = false;
    }

    private void startRecord() {
        if (!recordPermissionAccepted)
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        else {
            if (isPlaying) stopPlay();
            isRecording = true;
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(fileName);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                mediaRecorder.prepare();
            } catch (Exception e) {
                Log.e("startRecord", "prepare() failed");
            }

            mediaRecorder.start();
        }
    }

    private void stopRecord() {
        fileExist = true;
        buttonPlay.setEnabled(fileExist);
        buttonRemoveFile.setEnabled(fileExist);
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        isRecording = false;
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            stopPlay();
        }
        if (mediaRecorder != null) {
            stopRecord();
        }
    }
}
