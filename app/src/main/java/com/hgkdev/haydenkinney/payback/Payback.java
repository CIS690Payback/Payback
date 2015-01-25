package com.hgkdev.haydenkinney.payback;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by H on 1/25/2015.
 */
public class Payback extends Application {
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "PFIrWVFL7MJ1QIFkJ3WvX9DrXXtNOyzpa7njYBf0", "qXnCfX6cY3wWfm5IBefPXGxARVp61fubYJ0qlaxZ");
    }
}
