package com.example.edas.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.edas.adapter.EventRecycleViewAdapter;
import com.example.edas.R;
import com.example.edas.adapter.TabViewPagerAdapter;
import com.example.edas.data.Event;
import com.example.edas.fragment.TabFragment;
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

public class MainActivity extends AppCompatActivity {

    TabViewPagerAdapter tabViewPagerAdapter;
    TabLayout tabLayout;
    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabLayout);

        tabViewPagerAdapter = new TabViewPagerAdapter(getSupportFragmentManager(), this);

        viewPager.setAdapter(tabViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabViewPagerAdapter.createTabs();

        FloatingActionButton fab = findViewById(R.id.fabCreate);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EventActivity.class);
                intent.putExtra("action", "add");
                startActivityForResult(intent, 0);
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
            case 0:
                if (resultCode == RESULT_OK) {
                    Event event = new Event();
                    event.setDate(data.getExtras().getString("date"));
                    event.setTitle(data.getExtras().getString("title"));
                    event.setDescription(data.getExtras().getString("description"));
                    tabViewPagerAdapter.addEvent(event);
                }; break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Event event = new Event();
                    event.setId(data.getExtras().getLong("id"));
                    event.setDate(data.getExtras().getString("date"));
                    event.setTitle(data.getExtras().getString("title"));
                    event.setDescription(data.getExtras().getString("description"));
                    tabViewPagerAdapter.updateEvent(event);
                } break;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
