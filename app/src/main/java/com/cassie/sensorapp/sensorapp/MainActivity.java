package com.cassie.sensorapp.sensorapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startActivity(View view){
        Intent intent = null;

        switch(view.getId()) {
            case (R.id.sensor):
                intent = new Intent(this, MotionSensors.class);
                break;
            case (R.id.network):
                intent = new Intent(this, NetStats.class);
                break;
            case (R.id.about):
                intent = new Intent(this, AboutPhone.class);
                break;
            case (R.id.browser):
                intent = new Intent(this, NoPermissionsActivity.class);
        }
        startActivity(intent);
    }

    public void createShortcut(View view) {
        // remove facebook shortcut and replace with this one?
        
    }

}
