package com.example.uberclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MainActivity extends AppCompatActivity {
    String TAG = "tester";

    //Intent go to MapActivity
    public void go_MapActivity(){

        if(ParseUser.getCurrentUser().get("rideordrive").equals("rider")){
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(intent);
        }

    }

    //onclick enter button
    public void getStarted(View view) {
        Switch typeUserSwitch = findViewById(R.id.typeUserSwitch);
        String typeUser = "rider";
        if (typeUserSwitch.isChecked()){
            typeUser = "driver";
        }
        ParseUser.getCurrentUser().put("rideordrive",typeUser);
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    go_MapActivity();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check if user is signed in
        if (ParseUser.getCurrentUser() == null){
            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null){
                        Log.i(TAG,"Anonymous fail");
                    }else{
                        Log.i(TAG,"Anonymous sucess");
                    }
                }
            });
        }else if (ParseUser.getCurrentUser().get("rideordrive") != null){
            go_MapActivity();
        }
    }
}
