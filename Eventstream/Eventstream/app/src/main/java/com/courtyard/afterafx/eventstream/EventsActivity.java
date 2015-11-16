package com.courtyard.afterafx.eventstream;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.SaveCallback;

import java.util.Date;
import java.util.List;

public class EventsActivity extends ListActivity {

    private ParseQueryAdapter<Event> mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        getListView().setClickable(false);

        mainAdapter = new ParseQueryAdapter<Event>(this, Event.class);

        setListAdapter(mainAdapter);

//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
//        query.whereExists("eventName");
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> listViewEvents, ParseException e) {
//                if(e == null){
//                    Log.d("Events", "Retrived");
//                } else {
//                    Log.d("Events", "Error: " + e.getMessage());
//                }
//            }
//        });


        //Just put this here to test making objects in database
        //Will put a new event object in the database event table every time the EventsActivity is created.
        //So anytime you go the events page in our app it makes an event object and puts it in the database
        //----------------------------------------------------------------------------------------//
        Event eventConnect = new Event(); //Creates an event object and connects to the event table in database
        int eventId = (int) (Math.random() * 100000); //generate random eventId for now

        //This also works but it doesnt use the functions in the event class
//        eventConnect.put("eventId", eventId);
//        eventConnect.put("location", new ParseGeoPoint());
//        eventConnect.saveInBackground();

        //This uses the functions in the event class
        eventConnect.setEventId(eventId);
        eventConnect.setLocation(new ParseGeoPoint());
        eventConnect.setStartDate(new Date());
        eventConnect.setEndDate(new Date());
        /*
        Have to make a new SaveCallback() or else it doesnt work.
        Simply having eventConnect.saveInBackground(); doesnt work
        Which is weird b/c that works if we dont use the set function calls above and
            instead use the commented out eventConnect.put(...) methods.
         */
        eventConnect.saveInBackground(new SaveCallback() { //Saves the changes made on the database

            @Override
            public void done(ParseException e) {

            }

        });
    }
}
