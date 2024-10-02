package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class HeadsetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra("state")) {
            if (intent.getIntExtra("state", 0) == 0) {
                // Headset is unplugged
                Toast.makeText(context, "Headset Unplugged", Toast.LENGTH_SHORT).show();
            } else if (intent.getIntExtra("state", 0) == 1) {
                // Headset is plugged
                Toast.makeText(context, "Headset Plugged", Toast.LENGTH_SHORT).show();
            }
        }
    }
}