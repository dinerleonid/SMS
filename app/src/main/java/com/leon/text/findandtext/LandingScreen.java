package com.leon.text.findandtext;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.content.Intent;
import android.content.IntentFilter;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class LandingScreen extends AppCompatActivity implements View.OnClickListener,
        LocationListener, CompoundButton.OnCheckedChangeListener {

    private Button start, stop, stat;
    private Button select;
    public static TextView selectedRecipient, batLevel;
    private int level, criticalLevel;
    private LocationManager locationManager;
    private String providerName, custom_message;
    private Timer timer;
    private boolean gotLocation = false;
    private float myLAT;
    private float myLON;
    private SharedPreferences sp;
    private SharedPreferences.Editor edit;
    private BatteryHandler mBatteryLevelReceiver;
    private AlertDialog dialogExit;
    private SeekBar seekBar;
    private float discrete=0;
    private float startSeek=0;
    private float endSeek=100;
    private float start_pos=15;
    private int start_position=0;
    private AdView adView;
    private int levelFromBroadCast;
    private Switch onOffSwitch, customMessage;
    private ProgressBar progressBarRound;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_screen);

        sp = getSharedPreferences("textingprefs", MODE_PRIVATE);

        if (sp.getBoolean("firstTime", false) == false) {
            Intent gotToSplash = new Intent(LandingScreen.this, SplashScreen.class);
            startActivity(gotToSplash);
        }

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3315241718173584~4813802750");
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

//        start = (Button) findViewById(R.id.btn_start);
//        start.setOnClickListener(this);
//
//        stop = (Button) findViewById(R.id.btn_stop);
//        stop.setOnClickListener(this);

        select = (Button) findViewById(R.id.btn_select_recipient);
        select.setOnClickListener(this);



        startSeek=1;
        endSeek=100;

        start_position=(int) (((start_pos-startSeek)/(endSeek-startSeek))*100);
        discrete=start_pos;

        batLevel = (TextView) findViewById(R.id.textView_bat_level);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setProgress((int) start_pos);
        batLevel.setText(String.valueOf(seekBar.getProgress())+ "%");

        criticalLevel = seekBar.getProgress();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                batLevel.setText(String.valueOf((int) discrete) + "%");
                sp = getSharedPreferences("textingprefs", MODE_PRIVATE);
                edit = sp.edit();
                edit.putInt("criticalLevel", (int) discrete).commit();
                criticalLevel = (int) discrete;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {

                float temp=progress;
                float dis=endSeek-startSeek;
                discrete=(startSeek+((temp/100)*dis));
                if (discrete <= sp.getInt("level", 0) ) {
                    batLevel.setText(String.valueOf((int) discrete) + "%");
                }else{
                    seekBar.setClickable(false);
                    seekBar.setFocusable(false);
                    seekBar.setProgress(sp.getInt("level", 0) -1);
                    discrete = sp.getInt("level", 0) -1;
                   // seekBar.setEnabled(false);

                }
            }
        });


        selectedRecipient = (TextView) findViewById(R.id.textView_selected_recipient);

        stat = (Button) findViewById(R.id.stat);
        stat.setBackgroundColor(Color.parseColor("#FF7040"));
        //sp = PreferenceManager.getDefaultSharedPreferences(this);
        edit = sp.edit();
        selectedRecipient.setText(sp.getString("name", "").toString() + "\n" + sp.getString("number", "").toString());
        edit.putInt("criticalLevel", criticalLevel).commit();

        startBroadcast();
        edit.putBoolean("send", false).commit();
//        levelFromBroadCast=sp.getInt("level", 0);
       // if (criticalLevel >= levelFromBroadCast){
//            stopBatteryLocation();
        //}


        onOffSwitch = (Switch) findViewById(R.id.on_off_switch);
        onOffSwitch.setOnCheckedChangeListener(this);
        onOffSwitch.setChecked(false);

        customMessage = (Switch) findViewById(R.id.custom_message);
        customMessage.setOnCheckedChangeListener(this);
        //customMessage.setChecked(sp.getBoolean("customMessageSet", false));
        customMessage.setChecked(false);
        edit.putBoolean("customMessageSet", false).commit();

        progressBarRound = (ProgressBar) findViewById(R.id.progressBarRound);


//        if (sp.getBoolean("firstTime", true) == true) {
//            Intent gotToSplash = new Intent(LandingScreen.this, SplashScreen.class);
//            startActivity(gotToSplash);
//        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.exit_app:
                dialogExit = new AlertDialog.Builder(this).
                        setMessage(R.string.exit_app).
                        setTitle(R.string.confirm_exit).
                        setPositiveButton(R.string.yes_in_confirm_exit, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                if (sp != null){
                                    //edit.clear().commit();
                                }
                                if (mBatteryLevelReceiver != null) {
                                    unregisterReceiver(mBatteryLevelReceiver);
                                    stopBatteryLocation();
                                }
                                cancelNotification(getBaseContext(), 10);
                                finish();}
                        }).
                        setNegativeButton(R.string.no_in_confirm_exit, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                dialog.cancel();}
                        }).
                        setCancelable(false).
                        create();
                dialogExit.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case (R.id.btn_start):
////                if (mBatteryLevelReceiver == null){
////                    startBroadcast();
////                }
//                sp = PreferenceManager.getDefaultSharedPreferences(this);
//                edit=sp.edit();
//                Log.e(getString(R.string.critical_level_in_log), String.valueOf(criticalLevel));
//                if (sp.getString("number", "0").equals("0")){
//                    Toast.makeText(this, R.string.please_select_a_recipient, Toast.LENGTH_SHORT).show();
//
//
//                }else {
//                    startBroadcast();
//                    levelFromBroadCast = sp.getInt("level", 0);
//                    if (criticalLevel >= levelFromBroadCast) {
//                        edit.putBoolean("send", false).commit();
//
//                        Toast.makeText(this, getString(R.string.set_new_battery_value), Toast.LENGTH_SHORT).show();
//                    } else {
//                        edit.putBoolean("send", true).commit();
//
////                        sp = PreferenceManager.getDefaultSharedPreferences(this);
////                        edit=sp.edit();
//                       // edit.putBoolean("send", true);
//                       // startBroadcast();
//                       // PermissionsHandler.requestLocationPermissions(this);
//                        startLocation();
//                    }
//                }
//                break;
//            case (R.id.btn_stop):
//                stopBatteryLocation();
//                stat.setBackgroundColor(Color.parseColor("#E57373"));
//                cancelNotification(this, 10);
//                break;

            case (R.id.btn_select_recipient):

                boolean hasperm = PermissionsHandler.requestPermissions(this);
//                boolean hasPermissionsSend = PermissionsHandler.requestSendSmsPermissions(this);
//
//                boolean hasPermissionsRead = PermissionsHandler.requestContactsPermissions(this);
                if (hasperm){
                    openContacts();
                }

                break;

        }
    }

    public void openContacts(){
        Intent cont = new Intent(this, ImportContacts.class);
        startActivity(cont);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 88: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openContacts();
                } else {
                }
                return;
            }
            case 89: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocation();
                } else {
                }
                return;
            }
            case 90: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
        }
    }

    protected void startBroadcast() {
        mBatteryLevelReceiver = new BatteryHandler();
        registerReceiver(mBatteryLevelReceiver, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));


    }

    public void stopBatteryLocation() {
        try {
            unregisterReceiver(mBatteryLevelReceiver);
            mBatteryLevelReceiver = null;
            sp = getSharedPreferences("textingprefs", MODE_PRIVATE);
            level = 0;
            myLAT = 0;
            myLON = 0;
            edit.putFloat("lat", myLAT).putFloat("lon", myLON).commit();
            gotLocation = false;
            onOffSwitch.setChecked(false);
            customMessage.setChecked(false);
            progressBarRound.setProgress(View.INVISIBLE);
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(this);
            locationManager = null;
            Toast.makeText(this, R.string.location_battery_off,
                    Toast.LENGTH_SHORT).show();
        }catch (RuntimeException e){
        }
    }

    @Override
    public void onBackPressed(){
        Toast.makeText(this, getString(R.string.exit_via_menu) +
                "\n" + getString(R.string.or) + "\n" +
                getString(R.string.click_home_button), Toast.LENGTH_LONG)
                .show();
    }

    public void startLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        providerName = LocationManager.GPS_PROVIDER;
        try {
            locationManager.requestLocationUpdates(providerName, 100000, 100, this);
        }
        catch(SecurityException e){
            Log.e(getString(R.string.location_in_log), e.getMessage());
        }
        timer = new Timer("provider");
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(gotLocation == false){
                    try{
                        locationManager.removeUpdates(LandingScreen.this);
                        providerName = LocationManager.NETWORK_PROVIDER;
                        runOnUiThread( new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    locationManager.requestLocationUpdates(providerName, 100000, 100,
                                            LandingScreen.this);
                                }
                                catch (SecurityException e){
                                }
                            }
                        });
                    }catch(SecurityException e){
                        Log.e(getString(R.string.location_in_log2), e.getMessage());
                    }catch (NullPointerException e){
                        Log.e(getString(R.string.location_not_gound_in_log), e.getMessage());
                    }
                }
            }
        };
        timer.schedule(task, new Date(System.currentTimeMillis() + 1000));
    }

    @Override
    public void onLocationChanged(Location location) {
        if (gotLocation = true){
            stat.setBackgroundColor(Color.parseColor("#64B5F6"));
            progressBarRound.setVisibility(View.INVISIBLE);

            Toast.makeText(this, R.string.service_running, Toast.LENGTH_LONG).show();

        }else{
            progressBarRound.setVisibility(View.INVISIBLE);

            stat.setBackgroundColor(Color.parseColor("#FF7040"));
        }
        sp = getSharedPreferences("textingprefs", MODE_PRIVATE);

        edit = sp.edit();
        gotLocation = true;
        timer.cancel();
        DecimalFormat formatter = new DecimalFormat("0.00");

        myLAT = Float.parseFloat((formatter.format(location.getLatitude())));
        myLON = Float.parseFloat((formatter.format(location.getLongitude())));

        edit.putFloat("lat", myLAT).putFloat("lon", myLON).commit();
        stat.setBackgroundColor(Color.parseColor("#64B5F6"));
        //Toast.makeText(LandingScreen.this, getString(R.string.got_location_from) +
        //providerName + getString(R.string.two_dots) +
        //myLAT + getString(R.string.comma_in_got_location) + myLON, Toast.LENGTH_SHORT).show();
        applyStatusBar(getString(R.string.find_and_text), 10);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        progressBarRound.setVisibility(View.INVISIBLE);
        onOffSwitch.setChecked(false);

    }

    private void applyStatusBar(String iconTitle, int notificationId) {
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_app_48x48)
                .setContentTitle(iconTitle);
        Intent resultIntent = new Intent(this, LandingScreen.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_INSISTENT | Notification.FLAG_NO_CLEAR;

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(notificationId, notification);}

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager  nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case (R.id.on_off_switch):
            if (isChecked) {
                sp = getSharedPreferences("textingprefs", MODE_PRIVATE);
                edit = sp.edit();
                Log.e(getString(R.string.critical_level_in_log), String.valueOf(criticalLevel));
                if (sp.getString("number", "0").equals("0")) {
                    Toast.makeText(this, R.string.please_select_a_recipient, Toast.LENGTH_SHORT).show();
                    onOffSwitch.setChecked(false);

                } else {
                    startBroadcast();
                    levelFromBroadCast = sp.getInt("level", 0);
                    if (criticalLevel >= levelFromBroadCast) {
                        edit.putBoolean("send", false).commit();

                        Toast.makeText(this, getString(R.string.set_new_battery_value), Toast.LENGTH_SHORT).show();
                        onOffSwitch.setChecked(false);

                    } else {
                        LocationOnOffChecker checkLocation = new LocationOnOffChecker();
                        checkLocation.isLocationServiceEnabled(this);
                        edit.putBoolean("send", true).commit();
//                        sp = PreferenceManager.getDefaultSharedPreferences(this);
//                        edit=sp.edit();
                        // edit.putBoolean("send", true);
                        // startBroadcast();
                        // PermissionsHandler.requestLocationPermissions(this);
                        progressBarRound.setVisibility(View.VISIBLE);

                        startLocation();

                    }
                }
            } else {
                // onOffSwitch.setChecked(false);
                progressBarRound.setVisibility(View.INVISIBLE);

                stopBatteryLocation();
                stat.setBackgroundColor(Color.parseColor("#FF7040"));
                cancelNotification(this, 10);
                //progressBarRound.setProgress(View.INVISIBLE);

            }
                break;
            case (R.id.custom_message):
                sp = getSharedPreferences("textingprefs", MODE_PRIVATE);

                if (customMessage.isChecked()) {
                   // sp = getSharedPreferences("textingprefs", MODE_PRIVATE);
                    // customMessage.setChecked(true);
                    AlertDialog.Builder dialogMessage = new AlertDialog.Builder(this);
                    dialogMessage.setTitle(R.string.create_message_to_send);

// Set up the input
                    final EditText inputMessage = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    inputMessage.setInputType(InputType.TYPE_CLASS_TEXT);
                    dialogMessage.setView(inputMessage);
                    String inputSTR = sp.getString("custom_message", "");
                    inputMessage.setText(inputSTR);
                    dialogMessage.setView(inputMessage);


// Set up the buttons
                    dialogMessage.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //customMessage.setChecked(true);
                            custom_message = inputMessage.getText().toString();
                            if (!custom_message.toString().equals("")) {
                                edit.putString("custom_message", custom_message).commit();
                                edit.putBoolean("customMessageSet", true).commit();

                            }else{
                                customMessage.setChecked(false);
                            }
                        }
                    });
                    dialogMessage.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //controls state of custom message switch, either way must be off if custom message is empty
                            if (custom_message == null) {
                               // edit.putString("custom_message", custom_message).commit();
                                customMessage.setChecked(false);
                                edit.putBoolean("customMessageSet", false).commit();
                            }else{
                                customMessage.setChecked(false);
                                edit.putBoolean("customMessageSet", false).commit();

                                dialog.cancel();
                            }


                        }

                    });
                    dialogMessage.setCancelable(false);
                    dialogMessage.show();



//                if (custom_message == ""){
//                    customMessage.setChecked(false);
//                }else{
//                    customMessage.setChecked(true);
//                }
                    break;

                }else{
                    edit.putBoolean("customMessageSet", false).commit();
                }
        }

//        if (custom_message != ""){
//            customMessage.setChecked(false);
//        }else{
//            customMessage.setChecked(true);
//        }
    }


    public abstract class AdListener {
        public void onAdLoaded() {

        }

        public void onAdFailedToLoad(int errorCode) {

        }

        public void onAdOpened() {

        }

        public void onAdClosed() {

        }

        public void onAdLeftApplication() {

        }
    }




    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();

    }

    @Override
    public void onRestart(){
        super.onRestart();
        sp = getSharedPreferences("textingprefs", MODE_PRIVATE);
        seekBar.setProgress(sp.getInt("criticalLevel1", 1));
        boolean messageSwitch = sp.getBoolean("customMessageSet", false);
        customMessage.setChecked(messageSwitch);

    }


    @Override
    public void onStop(){
        super.onStop();
        sp = getSharedPreferences("textingprefs", MODE_PRIVATE);
        edit = sp.edit();
        edit.putInt("criticalLevel1", seekBar.getProgress()).commit();
        edit.putBoolean("customMessageSet", customMessage.isChecked()).commit();

    }

    /** Called when leaving the activity */

    @Override
    public void onPause() {
        sp = getSharedPreferences("textingprefs", MODE_PRIVATE);
        edit = sp.edit();
        if (adView != null) {
            adView.pause();

        }
        super.onPause();
        edit.putInt("criticalLevel1", seekBar.getProgress()).commit();
        edit.putBoolean("customMessageSet", customMessage.isChecked()).commit();
        //customMessage.setChecked(sp.getBoolean("customMessageSet", false));

    }

    @Override
    public void onStart(){
        super.onStart();
        customMessage.setChecked(sp.getBoolean("customMessageSet", false));
    }
}