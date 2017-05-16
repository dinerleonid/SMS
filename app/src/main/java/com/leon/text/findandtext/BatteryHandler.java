package com.leon.text.findandtext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

public class BatteryHandler extends BroadcastReceiver {
    private int level, status, criticalLevel;
     private SharedPreferences sp;
     private SharedPreferences.Editor edit;
    private Boolean send;


    @Override
    public void onReceive(Context context, Intent intent) {
        sp = context.getSharedPreferences("textingprefs", MODE_PRIVATE);
        edit = sp.edit();
        final Intent mIntent = context.getApplicationContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        status = mIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        level = mIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        edit.putInt("level", level).commit();
        criticalLevel = sp.getInt("criticalLevel", 15);
        send = sp.getBoolean("send", false);

//        int levelFromBroadCast=sp.getInt("level", 0);


        if (criticalLevel >= level && send == true){// && sp.getBoolean("send", false) == true){
            String number = sp.getString("number", "0");
            LandingScreen.cancelNotification(context, 10);
            SendSms sendSms = new SendSms(context);
            sendSms.sendSms(number);
            LandingScreen stopAfterSmsSent = new LandingScreen();
            stopAfterSmsSent.stopBatteryLocation();
            System.exit(0);

        }
    }
}
