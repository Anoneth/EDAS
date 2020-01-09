package com.example.edas.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edas.R;
import com.example.edas.callback.EventDiffCallback;
import com.example.edas.fragment.TabFragment;
import com.example.edas.misc.Tools;
import com.example.edas.activity.EventActivity;
import com.example.edas.data.Event;

import java.util.ArrayList;

public class EventRecycleViewAdapter extends RecyclerView.Adapter<EventRecycleViewAdapter.EventViewHolder> {
    class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textViewDate;
        private TextView textViewTitle;
        private Context context;

        EventViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            itemView.setOnClickListener(this);
        }

        void bind(Event event) {
            textViewDate.setText(Tools.getTime(event.getDate()));
            textViewTitle.setText(event.getTitle());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Event e = events.get(position);
                Intent intent = new Intent(context, EventActivity.class);
                intent.putExtra("action", "edit");
                intent.putExtra("id", e.getId());
                intent.putExtra("date", e.getDate().split(" ")[0]);
                intent.putExtra("time", e.getDate().split(" ")[1]);
                intent.putExtra("title", e.getTitle());
                intent.putExtra("description", e.getDescription());
                ((Activity) context).startActivityForResult(intent, 1);
            }
        }
    }

    private ArrayList<Event> events = new ArrayList<>();
    private TabFragment fragment;

    public EventRecycleViewAdapter(TabFragment fragment) {
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item_view, parent, false);
        Context context = parent.getContext();
        return new EventViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        holder.bind(events.get(position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void updateContent(ArrayList<Event> events) {
        EventDiffCallback diffCallback = new EventDiffCallback(this.events, events);
        Log.i("updateContent", this.events.size() + " " + events.size());
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.events.clear();
        this.events.addAll(events);
        diffResult.dispatchUpdatesTo(this);
    }

    public void removeEvent(int position) {
        fragment.removeEvent(events.get(position));
    }

    public void undoRemove() {
        fragment.undoRemove();
    }
}
