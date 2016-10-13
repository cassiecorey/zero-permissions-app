package com.cassie.sensorapp.sensorapp;

import android.app.ListActivity;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.widget.ArrayAdapter;

import java.util.List;
import java.util.ArrayList;

public class NetStats extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_stats);

        PackageManager pm = getPackageManager();
        List<ApplicationInfo> installedApps = pm.getInstalledApplications(pm.GET_META_DATA);

        ArrayList<String> listItems = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        setListAdapter(adapter);

        String uid,name,pk_rcv,pk_snd;
        Drawable icon;
        long rcv,snd;
        if (listItems.isEmpty()) {
            for (ApplicationInfo app : installedApps) {
                rcv = TrafficStats.getUidRxPackets(app.uid);
                snd = TrafficStats.getUidTxPackets(app.uid);
                if (rcv+snd>1) {
                    uid = Integer.toString(app.uid);
                    pk_rcv = Long.toString(rcv);
                    pk_snd = Long.toString(snd);
                    icon = pm.getApplicationIcon(app);
                    // stuff for getting the application name-is
                    String[] namesplit = app.toString().split(" ");
                    name = namesplit[namesplit.length - 1];
                    name = name.substring(0, name.length() - 1);
                    listItems.add("UID: " + uid + "\nNAME: " + name + "\nPACKETS SENT: " + pk_snd + "\nPACKETS RCV'd: " + pk_rcv);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

}
