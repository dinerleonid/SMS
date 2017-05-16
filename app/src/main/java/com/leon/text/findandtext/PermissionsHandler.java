package com.leon.text.findandtext;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionsHandler extends Activity{

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 88;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 89;
    private static final int MY_PERMISSIONS_REQUEST_TO_SEND_SMS = 90;
    private static Context context;
    public PermissionsHandler(Context context){
        this.context = context;
    }
//    public static boolean requestContactsPermissions(Context context) {
//        if (ContextCompat.checkSelfPermission(context,                Manifest.permission.READ_CONTACTS)                != PackageManager.PERMISSION_GRANTED) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                ActivityCompat.requestPermissions((Activity) context, new String[] {
//                                        android.Manifest.permission.READ_CONTACTS},
//                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
//            } else {
//            }
//            return false;
//        }
//        return true;
//    }

    public static boolean requestPermissions(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions((Activity) context, new String[] {
                                android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.SEND_SMS, android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            } else {
            }
            return false;
        }
        return true;
    }

//    public static void requestLocationPermissions(Context context) {
//        if (ContextCompat.checkSelfPermission(context,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                ActivityCompat.requestPermissions((Activity) context, new String[]
//                                {
//                                        android.Manifest.permission.ACCESS_FINE_LOCATION},
//                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//            } else {
//            }
//        }
//    }

//    public static boolean requestSendSmsPermissions(Context context) {
//        if (ContextCompat.checkSelfPermission(context,
//                Manifest.permission.SEND_SMS)
//                != PackageManager.PERMISSION_GRANTED) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                ActivityCompat.requestPermissions((Activity) context, new String[]{
//                                        android.Manifest.permission.SEND_SMS},
//                        MY_PERMISSIONS_REQUEST_TO_SEND_SMS);
//            } else {
//            }
//            return false;
//        }
//        return true;
//
//    }
}
