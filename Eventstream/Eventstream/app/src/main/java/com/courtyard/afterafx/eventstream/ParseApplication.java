package com.courtyard.afterafx.eventstream;

import android.app.Application;
import com.parse.*;



/**
 * Created by afterafx on 10/16/15.
 */
public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /*
        Must register all subclasses that inherit from ParseObject
        Doing this allows that class to be connected directly to the database table with the corresponding name
        Will eventually need to do the same for photo object and user object
         */
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(PhotoParse.class);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "jzne3U0W0NxkjF24gmuI0zZfZ0sRcXga9ag69ZfT", "84PuDG6RhBugWTautTFBdXVk3hb55a3PXd7O2eKr");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}