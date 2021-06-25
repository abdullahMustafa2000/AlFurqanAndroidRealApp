package com.islamicApp.AlFurkan.Classes;

import android.content.Context;

public class InternetConnection {

    Context context;

    public InternetConnection(Context context) {
        this.context = context;
    }

    public boolean internetIsConnected () {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

}
