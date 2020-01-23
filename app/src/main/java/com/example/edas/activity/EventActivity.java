package com.example.edas.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import com.example.edas.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EventActivity extends AppCompatActivity {

    long id = -1;
    boolean isNew;
    EditText editTextDate;
    EditText editTextTime;
    EditText editTextTitle;
    EditText editTextDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        editTextDate = ((EditText) findViewById(R.id.editTextDate));
        editTextTime = ((EditText) findViewById(R.id.editTextTime));
        editTextTitle = ((EditText) findViewById(R.id.editTextTitle));
        editTextDescription = ((EditText) findViewById(R.id.editTextDescription));

        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                editTextDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(myCalendar.getTime()));
            }
        };

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EventActivity.this, dateSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final TimePickerDialog timePickerDialog = new TimePickerDialog(EventActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                editTextTime.setText(String.format("%02d:%02d", hourOfDay, minute));
            }
        }, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);

        editTextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.setTitle(getString(R.string.pick_time));
                timePickerDialog.show();
            }
        });

        Button buttonSetAlarm = findViewById(R.id.buttonSetAlarm);

        String action = getIntent().getExtras().getString("action");
        if (action.equals("add")) {
            buttonSetAlarm.setEnabled(false);
            toolbar.setTitle(getString(R.string.action_add_event));
            isNew = true;
            editTextTitle.setInputType(InputType.TYPE_CLASS_TEXT);
            editTextDescription.setInputType(InputType.TYPE_CLASS_TEXT);
        } else if (action.equals("edit")) {
            buttonSetAlarm.setEnabled(true);
            isNew = false;
            if (getIntent().getExtras().containsKey("id"))
                id = getIntent().getExtras().getLong("id");
            String date = getIntent().getExtras().getString("date");
            String time = getIntent().getExtras().getString("time");
            String title = getIntent().getExtras().getString("title");
            String description = getIntent().getExtras().getString("description");
            toolbar.setTitle(getString(R.string.action_edit_event));
            editTextDate.setText(date);
            editTextTime.setText(time);
            editTextTitle.setText(title);
            editTextDescription.setText(description);
        }

        buttonSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, AlarmActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("date", editTextDate.getText().toString());
                intent.putExtra("time", editTextTime.getText().toString());
                intent.putExtra("title", editTextTitle.getText().toString());
                intent.putExtra("description", editTextDescription.getText().toString());
                startActivityForResult(intent, 12);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save: {
                boolean isOk = true;
                if (TextUtils.isEmpty(editTextDate.getText())) {
                    isOk = false;
                    editTextDate.setError(getString(R.string.not_empty));
                }
                if (TextUtils.isEmpty(editTextTime.getText())) {
                    isOk = false;
                    editTextTime.setError(getString(R.string.not_empty));
                }
                if (TextUtils.isEmpty(editTextTitle.getText())) {
                    isOk = false;
                    editTextTitle.setError(getString(R.string.not_empty));
                }
                if (isOk) {
                    String date = editTextDate.getText() + " " + editTextTime.getText();
                    String title = editTextTitle.getText().toString();
                    String description = editTextDescription.getText().toString();

                    Intent result = new Intent();

                    if (id != -1) {
                        result.putExtra("id", id);
                    }

                    result.putExtra("date", date);
                    result.putExtra("title", title);
                    result.putExtra("description", description);
                    setResult(RESULT_OK, result);
                    finish();
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
