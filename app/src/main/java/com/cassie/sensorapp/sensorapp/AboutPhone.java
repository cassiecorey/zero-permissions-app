package com.cassie.sensorapp.sensorapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class AboutPhone extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_phone);

        TextView batt = (TextView) findViewById(R.id.batt);

        FileInputStream inputStream;
        InputStreamReader isr;
        BufferedReader bufferedReader;
        String readString = null;

        try {
            inputStream = new FileInputStream(new File("/sys/class/power_supply/battery/status"));
            isr = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(isr);
            readString = bufferedReader.readLine();
            batt.setText(batt.getText()+readString);
            isr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
