package com.example.edas.adapter;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.example.edas.R;
import com.example.edas.data.Event;
import com.example.edas.data.EventDbHelper;
import com.example.edas.fragment.TabFragment;
import com.example.edas.misc.Tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TabViewPagerAdapter extends FragmentStatePagerAdapter {

    private final ArrayList<TabFragment> fragments = new ArrayList<>();
    private final ArrayList<String> titles = new ArrayList<>();
    private final FragmentManager fragmentManager;

    private boolean isNew = false;

    EventDbHelper dbHelper;
    Context context;
    Event removedEvent;

    public TabViewPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
        dbHelper = new EventDbHelper(context);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getTitle();
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    public void createTabs() {
        isNew = false;
        fragments.clear();
        titles.clear();
        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date now = new Date();


        ArrayList<String> tabTitles = dbHelper.getDatesAfter(dateFormat1.format(now));

        ArrayList<Event> pastEvents = dbHelper.getEventsBefore(dateFormat2.format(now));
        if (pastEvents.size() > 0) {
            fragments.add(getFragment(context.getString(R.string.past)));
            fragments.get(0).updateContent(pastEvents);
            titles.add(context.getString(R.string.past));
        }
        for (String title : tabTitles) {
            TabFragment fragment = getFragment(Tools.getDate(title, context));
            fragments.add(fragment);
            fragment.updateContent(dbHelper.getEventsAtDay(title));
        }
        titles.addAll(tabTitles);
        for (TabFragment fragment : fragments) {
            Log.i("TabViewPagerAdapter", "fragment " + fragment.getTitle() + " isAdded: " + fragment.isAdded());
        }
        Log.i("TabViewPagerAdapter", "notifyDataSetChanged");
        notifyDataSetChanged();
    }

    public void addEvent(Event event) {
        dbHelper.createEvent(event);
    }

    public void updateEvent(Event event) {
        dbHelper.updateEvent(event);
        createTabs();
    }

    public void removeEvent(Event event) {
        dbHelper.removeEvent(event.getId());
        removedEvent = event;
        createTabs();
    }

    public void undoRemove() {
        dbHelper.createEvent(removedEvent);
        removedEvent = null;
        createTabs();
    }

    public void prepareTabs() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (TabFragment fragment : fragments) {
            transaction.remove(fragment);
        }
        transaction.commit();
    }

    public void restoreTabs() {
        createTabs();
    }

    private TabFragment getFragment(String title) {
        List<Fragment> fragmentList = fragmentManager.getFragments();
        Log.i("TabViewPagerAdapter", "finding fragment " + title);
        for (Fragment fragment : fragmentList) {
            Log.i("TabViewPagerAdapter", "found fragment " + ((TabFragment) fragment).getTitle());
            if (((TabFragment) fragment).getTitle().equals(title)) {
                return (TabFragment) fragment;
            }
        }
        isNew = true;
        return TabFragment.getInstance(this, title, "");
    }
}
