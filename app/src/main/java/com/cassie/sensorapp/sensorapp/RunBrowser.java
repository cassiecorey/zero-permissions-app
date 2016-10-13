package com.cassie.sensorapp.sensorapp;

/**
 * Created by cassiancorey on 10/5/16.
 */
public class RunBrowser implements Runnable {
    private String data;
    private NoPermissionsActivity parent;

    public RunBrowser() {
        super();
    }

    public RunBrowser(NoPermissionsActivity parent, String data) {
        super();
        this.data = data;
        this.parent = parent;
    }

    public void run() {
        parent.sendByBrowser(data);
    }

}
