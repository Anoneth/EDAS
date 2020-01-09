package com.example.edas.data;


import android.provider.BaseColumns;

import java.util.Date;

public class Event {
    long id;
    String date;
    String title;
    String description;

    public final static String TABLE_NAME = "event";

    public final static String _ID = BaseColumns._ID;
    public final static String COLUMN_DATE = "event_date";
    public final static String COLUMN_TITLE = "event_title";
    public final static String COLUMN_DESC = "event_desc";

    public Event() {}

    public Event(String date, String title, String description) {
        this.date = date;
        this.title = title;
        this.description = description;
    }

    public Event(int id, String date, String title, String description) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
