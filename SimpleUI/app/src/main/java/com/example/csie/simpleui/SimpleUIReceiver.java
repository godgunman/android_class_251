package com.example.csie.simpleui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SimpleUIReceiver extends BroadcastReceiver {
    public SimpleUIReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d("debug", "SimpleUIReceiver.onReceive");
    }
}
