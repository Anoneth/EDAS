package com.example.edas.callback;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edas.R;
import com.example.edas.adapter.EventRecycleViewAdapter;
import com.google.android.material.snackbar.Snackbar;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private EventRecycleViewAdapter eventRecycleViewAdapter;
    private Activity activity;

    public SwipeToDeleteCallback(EventRecycleViewAdapter eventRecycleViewAdapter, Activity activity) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.eventRecycleViewAdapter = eventRecycleViewAdapter;
        this.activity = activity;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        eventRecycleViewAdapter.removeEvent(position);
        View view = activity.findViewById(R.id.main_layout);
        Snackbar snackbar = Snackbar.make(view, R.string.action_undo, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.action_undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventRecycleViewAdapter.undoRemove();
            }
        });
        snackbar.show();
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }
}
