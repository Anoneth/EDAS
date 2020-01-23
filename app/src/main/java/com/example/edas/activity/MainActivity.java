package com.example.edas.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.edas.R;
import com.example.edas.adapter.TabViewPagerAdapter;
import com.example.edas.data.Event;
import com.example.edas.data.EventDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final static int ACTION_EVENT_NEW = 0;
    private final static int ACTION_EVENT_EDIT = 1;
    private final static int ACTION_TOKEN_GET = 2;

    private final static int MENU_LOGIN = 0;
    private final static int MENU_SAVE = 1;
    private final static int MENU_LOAD = 2;
    private final static int MENU_LOGOUT = 3;

    TabViewPagerAdapter tabViewPagerAdapter;
    TabLayout tabLayout;
    ViewPager viewPager;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabLayout);

        tabViewPagerAdapter = new TabViewPagerAdapter(getSupportFragmentManager(), this, viewPager);

        viewPager.setAdapter(tabViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabViewPagerAdapter.createTabs();

        FloatingActionButton fab = findViewById(R.id.fabCreate);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EventActivity.class);
                intent.putExtra("action", "add");
                startActivityForResult(intent, ACTION_EVENT_NEW);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        tabViewPagerAdapter.restoreTabs();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTION_EVENT_NEW:
                if (resultCode == RESULT_OK) {
                    Event event = new Event();
                    event.setDate(data.getExtras().getString("date"));
                    event.setTitle(data.getExtras().getString("title"));
                    event.setDescription(data.getExtras().getString("description"));
                    tabViewPagerAdapter.addEvent(event);
                }
                ;
                break;
            case ACTION_EVENT_EDIT:
                if (resultCode == RESULT_OK) {
                    Event event = new Event();
                    event.setId(data.getExtras().getLong("id"));
                    event.setDate(data.getExtras().getString("date"));
                    event.setTitle(data.getExtras().getString("title"));
                    event.setDescription(data.getExtras().getString("description"));
                    tabViewPagerAdapter.updateEvent(event);
                }
                break;
            case ACTION_TOKEN_GET:
                if (resultCode == RESULT_OK) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", data.getExtras().getString("token"));
                    editor.apply();
                }
            default:
                ;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (!checkToken()) {
            menu.add(0, MENU_LOGIN, Menu.NONE, R.string.action_login);
        } else {
            menu.add(0, MENU_SAVE, Menu.NONE, R.string.action_save);
            menu.add(0, MENU_LOAD, Menu.NONE, R.string.action_load);
            menu.add(0, MENU_LOGOUT, Menu.NONE, R.string.action_logout);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case MENU_LOGIN:
                startLogin();
                break;
            case MENU_SAVE:
                saveEvents();
                break;
            case MENU_LOAD:
                loadEvents();
                break;
            case MENU_LOGOUT:
                removeToken();
                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    private void startLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, ACTION_TOKEN_GET);
    }

    private boolean checkToken() {
        sharedPreferences = getSharedPreferences("some_important", Context.MODE_PRIVATE);
        Log.i("checkToken", sharedPreferences.contains("token") + " " + sharedPreferences.getString("token", "empty"));
        return sharedPreferences.contains("token");
    }

    private void saveEvents() {
        final EventDbHelper dbHelper = new EventDbHelper(this);
        ArrayList<Event> events = dbHelper.getAllEvents();
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("token", sharedPreferences.getString("token", ""));
            JSONArray jsonArray = new JSONArray();
            for (Event event : events) {
                jsonArray.put(event.toJson());
            }
            jsonObject.put("events", jsonArray);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            //String url = "http://ptsv2.com/t/3lkem-1579764783/post";
            String url = LoginActivity.SERVER_ADDRESS + LoginActivity.SERVER_ADDRESS_SAVE;
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.has("status")) {
                            if (response.getInt("status") == 1) {
                                Toast.makeText(MainActivity.this, R.string.success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, R.string.try_later, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception ex) {
                        Toast.makeText(MainActivity.this, R.string.try_later, Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("onErrorResponseSave", error.getMessage());
                    Toast.makeText(MainActivity.this, R.string.try_later, Toast.LENGTH_SHORT).show();
                }
            });
            requestQueue.add(request);
        } catch (Exception ex) {
            Log.e("saveEvents", ex.getMessage());
            Toast.makeText(this, R.string.internal_error, Toast.LENGTH_SHORT).show();
        }
        dialog.cancel();
    }

    private void loadEvents() {
        final EventDbHelper dbHelper = new EventDbHelper(this);
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("token", sharedPreferences.getString("token", ""));
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = LoginActivity.SERVER_ADDRESS + LoginActivity.SERVER_ADDRESS_SAVE;
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.has("status")) {
                            if (response.getInt("status") == 1) {
                                JSONArray jsonArray = response.getJSONArray("events");
                                ArrayList<Event> events = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    events.add(Event.fromJson(jsonArray.getJSONObject(i)));
                                }
                                dbHelper.removeEvents();
                                dbHelper.addEvents(events);
                                tabViewPagerAdapter.createTabs();
                                Toast.makeText(MainActivity.this, R.string.success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, R.string.try_later, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception ex) {
                        Toast.makeText(MainActivity.this, R.string.try_later, Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("onErrorResponseSave", error.getMessage());
                    Toast.makeText(MainActivity.this, R.string.try_later, Toast.LENGTH_SHORT).show();
                }
            });
            requestQueue.add(request);
        } catch (Exception ex) {
            Log.e("loadEvents", ex.getMessage());
            Toast.makeText(this, R.string.internal_error, Toast.LENGTH_SHORT).show();
        }
        dialog.cancel();
    }

    private void removeToken() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("token");
        editor.apply();
        startLogin();
    }
}
