package com.example.edas.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.edas.R;
import com.example.edas.adapter.EventRecycleViewAdapter;
import com.example.edas.adapter.TabViewPagerAdapter;
import com.example.edas.data.Event;
import com.example.edas.callback.SwipeToDeleteCallback;

import java.util.ArrayList;

public class TabFragment extends Fragment {
    private EventRecycleViewAdapter eventRecycleViewAdapter = new EventRecycleViewAdapter(this);
    private TabViewPagerAdapter tabViewPagerAdapter;
    private String title;
    private String date;

    public static final TabFragment getInstance(TabViewPagerAdapter tabViewPagerAdapter, String title, String date) {
        TabFragment fragment = new TabFragment();
        fragment.setTitle(title);
        fragment.setDate(date);
        Bundle bundle = new Bundle(1);
        bundle.putString("title", title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            title = getArguments().getString("title");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(eventRecycleViewAdapter, getActivity()));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(eventRecycleViewAdapter);

        tabViewPagerAdapter = (TabViewPagerAdapter) ((ViewPager) getActivity().findViewById(R.id.viewpager)).getAdapter();

        return view;
    }

    public void removeEvent(Event event) {
        tabViewPagerAdapter.removeEvent(event);
    }

    public void undoRemove() {
        tabViewPagerAdapter.undoRemove();
    }

    public void updateContent(ArrayList<Event> events) {
        eventRecycleViewAdapter.updateContent(events);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }
}
