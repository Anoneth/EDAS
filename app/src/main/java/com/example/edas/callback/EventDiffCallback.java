package com.example.edas.callback;

import androidx.recyclerview.widget.DiffUtil;

import com.example.edas.data.Event;

import java.util.List;

public class EventDiffCallback extends DiffUtil.Callback {
    private final List<Event> oldEvents;
    private final List<Event> newEvents;

    public EventDiffCallback(List<Event> oldEvents, List<Event> newEvents) {
        this.oldEvents = oldEvents;
        this.newEvents = newEvents;
    }

    @Override
    public int getOldListSize() {
        return oldEvents.size();
    }

    @Override
    public int getNewListSize() {
        return newEvents.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Event oldEvent = oldEvents.get(oldItemPosition);
        Event newEvent = newEvents.get(newItemPosition);
        return (oldEvent.getId() == newEvent.getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Event oldEvent = oldEvents.get(oldItemPosition);
        Event newEvent = newEvents.get(newItemPosition);
        return (oldEvent.getDate().equals(newEvent.getDate())
                && oldEvent.getTitle().equals(newEvent.getTitle())
                && oldEvent.getDescription().equals(newEvent.getDescription()));
    }

}
