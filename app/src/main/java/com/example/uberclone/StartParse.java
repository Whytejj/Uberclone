package com.example.uberclone;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class StartParse extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("1694e50f0b2cc301e3f9bdab64fa81dfd6d58548")
                // if defined
                .clientKey("37a62bafbdcc07bfc2012c6086882e99bba061b9") //AKA masterkey
                .server("http://13.59.24.114:80/parse/")
                .build()
        );

        /*
        ParseObject object = new ParseObject("ExampleObject");
        object.put("myNumber", "234");
        object.put("myString", "jay");

        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException ex) {
                if (ex == null) {
                    Log.i("parsesetup", "Parse Successful!");
                } else {
                    Log.i("parsesetup", "Parse Failed" + ex.toString());
                }
            }
        });
        */



        //ParseUser.enableAutomaticUser();

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

    }
}
