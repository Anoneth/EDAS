package com.example.edas;

import android.util.Log;

import com.example.edas.activity.MainActivity;
import com.example.edas.data.Event;


import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class ExampleUnitTest {

    @Test
    public void event_isCorrect() throws Exception {
        Event event = new Event();
        event.setTitle("123title");
        event.setDate("2020-12-29 23:22");
        event.setDescription("some important");
        Event event2 = new Event(event.getDate(), event.getTitle(), event.getDescription());
        assertEquals(event.getDate(), event2.getDate());
        assertEquals(event.getTitle(), event2.getTitle());
        assertEquals(event.getDescription(), event2.getDescription());
    }
}