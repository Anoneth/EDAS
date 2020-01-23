package com.example.edas.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "event.db";
    private static final int DATABASE_VERSION = 1;

    public EventDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_DB = "CREATE TABLE " + Event.TABLE_NAME
                + "(" + Event._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Event.COLUMN_DATE + " DATE NOT NULL, "
                + Event.COLUMN_TITLE + " TEXT NOT NULL, "
                + Event.COLUMN_DESC + " TEXT);";
        db.execSQL(SQL_CREATE_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addEvents(ArrayList<Event> events) {
        for (Event event : events) {
            createEvent(event);
        }
    }

    public void removeEvents() {
        getWritableDatabase().execSQL("DELETE FROM " + Event.TABLE_NAME);
    }

    public long createEvent(Event event) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Event.COLUMN_DATE, event.getDate());
        contentValues.put(Event.COLUMN_TITLE, event.getTitle());
        contentValues.put(Event.COLUMN_DESC, event.getDescription());
        return getWritableDatabase().insert(Event.TABLE_NAME, null, contentValues);
    }

    public Event getEvent(long id) {
        Event event = null;
        String select = "SELECT * FROM " + Event.TABLE_NAME + " WHERE " + Event._ID + " = " + id;
        Cursor cursor = getReadableDatabase().rawQuery(select, null);
        if (cursor.moveToFirst()) {
            event = new Event();
            event.setId(id);
            event.setDate(cursor.getString(cursor.getColumnIndex(Event.COLUMN_DATE)));
            event.setTitle(cursor.getString(cursor.getColumnIndex(Event.COLUMN_TITLE)));
            event.setDescription(cursor.getString(cursor.getColumnIndex(Event.COLUMN_DESC)));
        }
        cursor.close();
        return event;
    }

    public ArrayList<Event> getAllEvents() {
        ArrayList<Event> events = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String select = "SELECT * FROM " + Event.TABLE_NAME + " ORDER BY " + Event.COLUMN_DATE + " ASC";
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst())
            do {
                Event event = new Event();
                event.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Event._ID))));
                event.setDate(cursor.getString(cursor.getColumnIndex(Event.COLUMN_DATE)));
                event.setTitle(cursor.getString(cursor.getColumnIndex(Event.COLUMN_TITLE)));
                event.setDescription(cursor.getString(cursor.getColumnIndex(Event.COLUMN_DESC)));
                events.add(event);
            } while (cursor.moveToNext());
        cursor.close();
        return events;
    }

    public boolean updateEvent(Event event) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Event.COLUMN_DATE, event.getDate());
        contentValues.put(Event.COLUMN_TITLE, event.getTitle());
        contentValues.put(Event.COLUMN_DESC, event.getDescription());
        int result = getWritableDatabase().update(Event.TABLE_NAME,
                contentValues, Event._ID + " = ?",
                new String[]{String.valueOf(event.getId())});
        return result > 0;
    }

    public boolean removeEvent(long id) {
        String select = "DELETE FROM " + Event.TABLE_NAME + " WHERE "
                + Event._ID + " = " + id;
        int result = getWritableDatabase().delete(Event.TABLE_NAME,
                Event._ID + " = ?",
                new String[]{String.valueOf(id)});
        return result > 0;
    }

    public ArrayList<String> getDatesAfter(String date) {
        ArrayList<String> strings = new ArrayList<>();
        String select = "SELECT substr(" + Event.COLUMN_DATE + ", 0, 11) AS sub"
                + " FROM " + Event.TABLE_NAME
                + " WHERE " + Event.COLUMN_DATE + " > strftime(\'%Y-%m-%d %H:%M\', \'now\')"
                + " AND sub >= ?" + " GROUP BY sub ORDER BY sub ASC";

        Cursor cursor = getReadableDatabase().rawQuery(select, new String[] {date});
        if (cursor.moveToFirst())
            do {
                strings.add(cursor.getString(0));
            } while (cursor.moveToNext());
        cursor.close();
        return strings;
    }

    public ArrayList<Event> getEventsBefore(String date) {
        Log.i("getEventsBefore", date);
        ArrayList<Event> events = new ArrayList<>();
        String select = "SELECT * FROM " + Event.TABLE_NAME
                + " WHERE " + Event.COLUMN_DATE + " < ? "
                + "ORDER BY + " + Event.COLUMN_DATE + " DESC";
        Cursor cursor = getReadableDatabase().rawQuery(select, new String[] {date});
        if (cursor.moveToFirst())
            do {
                Event event = new Event();
                event.setId(cursor.getLong(cursor.getColumnIndex(Event._ID)));
                event.setDate(cursor.getString(cursor.getColumnIndex(Event.COLUMN_DATE)));
                event.setTitle(cursor.getString(cursor.getColumnIndex(Event.COLUMN_TITLE)));
                event.setDescription(cursor.getString(cursor.getColumnIndex(Event.COLUMN_DESC)));
                events.add(event);
            } while (cursor.moveToNext());
        cursor.close();
        Log.i("getEventsBeforeCount", events.size() + "");
        return events;
    }

    public ArrayList<Event> getEventsAtDay(String day) {
        ArrayList<Event> events = new ArrayList<>();
        String select = "SELECT * FROM " + Event.TABLE_NAME
                + " WHERE " + Event.COLUMN_DATE + " > strftime(\'%Y-%m-%d %H:%M\', \'now\')"
                +" AND substr(" + Event.COLUMN_DATE + ", 0, 11) = ?"
                + " ORDER BY + " + Event.COLUMN_DATE + " ASC";
        Cursor cursor = getReadableDatabase().rawQuery(select, new String[] {day});
        Log.i("getEventsAtDay", day);
        if (cursor.moveToFirst())
            do {
                Event event = new Event();
                event.setId(cursor.getLong(cursor.getColumnIndex(Event._ID)));
                event.setDate(cursor.getString(cursor.getColumnIndex(Event.COLUMN_DATE)));
                event.setTitle(cursor.getString(cursor.getColumnIndex(Event.COLUMN_TITLE)));
                event.setDescription(cursor.getString(cursor.getColumnIndex(Event.COLUMN_DESC)));
                events.add(event);
            } while (cursor.moveToNext());
        cursor.close();
        return events;
    }

}
