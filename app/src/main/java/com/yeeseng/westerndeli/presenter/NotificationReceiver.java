package com.yeeseng.westerndeli.presenter;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


public class NotificationReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Log.e("RECEIVE0","RUNNING");
        ComponentName comp = new ComponentName(context.getPackageName(),
                NotificationIntentService.class.getName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationIntentService.enqueueWork(context, (intent.setComponent(comp)));
        } else {
            context.startService(new Intent(context, NotificationIntentService.class));
        }
    }

}