package com.cassie.sensorapp.sensorapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.content.Context;

import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.Date;

public class MotionSensors extends AppCompatActivity implements SensorEventListener{

    private SensorManager mSensorManager;
    private Sensor accSensor;
    private Sensor gyrSensor;
    private Sensor magSensor;

    private long lastUpdate;

    private TextView text;

    private String accData = "";
    private String gyrData = "";
    private String magData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_sensors);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyrSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        text = (TextView)findViewById(R.id.text_view);
    }

    public void startLogging(View view) {
        Button start_button = (Button)findViewById(R.id.start_button);
        TextView text = (TextView)findViewById(R.id.text_view);

        start_button.setBackgroundColor(Color.parseColor("#00b200"));

        // start logging sensor data
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        text.setText(currentDateTimeString);

        mSensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, gyrSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, magSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopLogging(View view) {
        Button start_button = (Button)findViewById(R.id.start_button);

        start_button.setBackgroundColor(Color.parseColor("#00ff00"));

        // stop logging sensor data
        mSensorManager.unregisterListener(this);
    }

    public void sendData(View view) {
        // send sensor data
        String filename = String.format("%s",lastUpdate);
        String string = text.getText().toString();
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
            text.setText("SAVED TO INTERNAL STORAGE");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub
    }

    public void onSensorChanged(SensorEvent event) {
        // Log it every 5 seconds
//        long curTime = System.currentTimeMillis();
//        if((curTime - lastUpdate) > 5000) {
//            lastUpdate = curTime;
        text.setText(String.format("%s\n%s,%s,%s,%s",
                text.getText(),event.sensor.getName(),
                event.values[0],event.values[1],event.values[2]));
//        }

    }



}
