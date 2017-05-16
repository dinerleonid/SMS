package com.leon.text.findandtext;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;


public class SendSms extends Activity{

    private static Context context;
    private String messageDefault, customMessage, message;
    private SharedPreferences sp;

    public SendSms(Context context){
        this.context = context;
    }

    public void sendSms(String phoneNumber) {
        sp = context.getSharedPreferences("textingprefs", context.MODE_PRIVATE);
        float latLink = sp.getFloat("lat", 0);
        float lonLink = sp.getFloat("lon", 0);
        String uri = "http://maps.google.com/maps?daddr=" + latLink + "," + lonLink;
        messageDefault = context.getString(R.string.hi_my_battery_location) +"\n" + context.getString(R.string.my_last_location) + "\n" + uri;

        customMessage = sp.getString("custom_message", "");



        try {
            SmsManager sms = SmsManager.getDefault();

            if (customMessage != "" && sp.getBoolean("customMessageSet", false)){
                message = customMessage + "\n" + context.getString(R.string.my_last_location) + "\n" + uri;
            }else{
                message = messageDefault;
            }

           // sms.sendTextMessage(phoneNumber, null, message, null, null);


            ArrayList<String> parts = sms.divideMessage(message);
            sms.sendMultipartTextMessage(phoneNumber, null, parts, null, null);


        }catch (SecurityException e) {
                Log.e("From SendSms SecurityEx", e.toString());
//             Toast.makeText(context, getString(R.string.sms_cant_be_sent) +
//                     getString(R.string.select_recipient_and_allow), Toast.LENGTH_LONG).show();
        }catch (RuntimeException e){
                Log.e("From SendSms RuntimeExe", e.toString());
//             Toast.makeText(context, getString(R.string.sms_cant_be_sent) +
//                     getString(R.string.select_recipient_and_allow), Toast.LENGTH_LONG).show();
        }
    }
}
