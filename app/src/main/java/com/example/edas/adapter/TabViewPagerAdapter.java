package com.example.edas.adapter;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

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
    private final FragmentManager fragmentManager;

    private EventDbHelper dbHelper;
    private Context context;
    private Event removedEvent;
    private ViewPager viewPager;

    public TabViewPagerAdapter(FragmentManager fragmentManager, Context context, ViewPager viewPager) {
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
        dbHelper = new EventDbHelper(context);
        this.context = context;
        this.viewPager = viewPager;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String tmp = fragments.get(position).getDate();
        if (tmp.equals("past")) return context.getString(R.string.past);
        return Tools.getDate(fragments.get(position).getDate(), context);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    /*@Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }*/

    public void createTabs() {
        fragments.clear();
        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date now = new Date();

        ArrayList<String> tabTitles = dbHelper.getDatesAfter(dateFormat1.format(now));

        ArrayList<Event> pastEvents = dbHelper.getEventsBefore(dateFormat2.format(now));
        if (pastEvents.size() > 0) {
            fragments.add(getFragment("past", context.getString(R.string.past)));
            fragments.get(0).updateContent(pastEvents);
        }
        for (String title : tabTitles) {
            TabFragment fragment = getFragment(title, Tools.getDate(title, context));
            fragments.add(fragment);
            fragment.updateContent(dbHelper.getEventsAtDay(title));
        }
        notifyDataSetChanged();
        if (viewPager.getCurrentItem() + 1 > fragments.size()) viewPager.setCurrentItem(fragments.size() - 1);
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

    public void restoreTabs() {
        createTabs();
    }

    private TabFragment getFragment(String date, String title) {
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            if (((TabFragment) fragment).getDate().equals(date)) {
                return (TabFragment) fragment;
            }
        }
        return TabFragment.getInstance(this, title, date);
    }
}
