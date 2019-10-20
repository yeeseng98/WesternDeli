package com.yeeseng.westerndeli.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;

import com.yeeseng.westerndeli.R;
import com.yeeseng.westerndeli.presenter.NotificationReceiver;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.notiBt)
    Switch notiSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Notifications", MODE_PRIVATE);
        Boolean isNotified = prefs.getBoolean("isNotified", true);

        if(isNotified){
            notiSwitch.setChecked(true);
        } else{
            notiSwitch.setChecked(false);
        }
    }

    @OnClick(R.id.backBt)
    public void back(){
        finish();
    }

    @OnClick(R.id.notiBt)
    public void notiSwitching(){
        if(notiSwitch.isChecked()){
            //user shuts off notifications
            SharedPreferences.Editor editor = getSharedPreferences("Notifications", MODE_PRIVATE).edit();
            editor.putBoolean("isNotified", true).commit();
        } else{
            SharedPreferences.Editor editor = getSharedPreferences("Notifications", MODE_PRIVATE).edit();
            editor.putBoolean("isNotified", false).commit();
            cancelNotification(0);
        }
    }

    public void cancelNotification(int requestCode) {
        try {
            Intent notificationIntent = new Intent(getApplicationContext(), NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
