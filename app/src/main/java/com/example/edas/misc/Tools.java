package com.example.edas.misc;

import android.content.Context;
import android.util.Log;

import com.example.edas.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tools {
    public static String getDate(String date, Context context) {
        Date now = new Date();
        SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd", context.getResources().getConfiguration().locale);
        Date sourceDate = new Date();
        try {
            sourceDate = source.parse(date);
        } catch (Exception ex) {
            Log.e("Tools.getDate()", ex.getMessage());
        }
        String format = "";
        SimpleDateFormat year = new SimpleDateFormat("yyyy", context.getResources().getConfiguration().locale);
        int y1 = Integer.valueOf(year.format(sourceDate));
        int y2 = Integer.valueOf(year.format(now));
        if (y1 != y2) format = "d MMM yy";
        else {
            SimpleDateFormat month = new SimpleDateFormat("M", context.getResources().getConfiguration().locale);
            int m1 = Integer.valueOf(month.format(sourceDate));
            int m2 = Integer.valueOf(month.format(now));
            if (m1 == m2) {
                SimpleDateFormat day = new SimpleDateFormat("d", context.getResources().getConfiguration().locale);
                int d1 = Integer.valueOf(day.format(sourceDate));
                int d2 = Integer.valueOf(day.format(now));
                if (d1 == d2 + 1) return context.getString(R.string.tomorrow);
                if (d1 == d2) return context.getString(R.string.today);
                if (d1 == d2 - 1) return context.getString(R.string.yesterday);
            }
            format = "d MMM";
        }
        SimpleDateFormat result = new SimpleDateFormat(format, context.getResources().getConfiguration().locale);
        return result.format(sourceDate);
    }

    public static String getTime(String date, Context context) {
        long nowDate = new Date().getTime();
        Date sourceDate = new Date();
        SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            sourceDate = source.parse(date);
        } catch (Exception ex) {
            Log.e("Tools.getDate()", ex.getMessage());
        }
        long eventDate = sourceDate.getTime();
        Log.i("getTime", date);
        if (nowDate > eventDate) return getDate(date, context) + ", " + date.split(" ")[1];
        return date.split(" ")[1];
    }

    public static String dateTimeToString(Date date, Context context) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm", context.getResources().getConfiguration().locale);
        return dateFormat.format(date);
    }
}
