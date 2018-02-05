package com.example.mustufa.deamonapp.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.mustufa.deamonapp.R;
import com.example.mustufa.deamonapp.Services.LocationUpdateService;
import com.example.mustufa.deamonapp.Services.MyService;
import com.example.mustufa.deamonapp.modal.CustomPhoneStateListener;

public class MainActivity extends AppCompatActivity {

    //Vars..
    TelephonyManager telephonyManager;
    public static String deviceID;
    public static boolean gotIMEI = false;
    private LocationManager locationManager;
    public static boolean permissonGranted = false;
    AlertDialog alert;
    AlertDialog.Builder alertDialogBuilder;
    private boolean gotGPSenabled = false;


    //widgets

    //Constants
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        EnabledGPS();
        gettingIMEIPermission();
        getLocationPermission();
        if(gotIMEI) {
            gettingIMEI();
        }

        if(permissonGranted) {
            EnabledGPS();
        }

        if (permissonGranted && EnabledGPS()) {


            Intent intent = new Intent(MainActivity.this, MyService.class);
            startService(intent);
            Intent locationUpdateIntent = new Intent(MainActivity.this,
                    LocationUpdateService.class);
            startService(locationUpdateIntent);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (permissonGranted && EnabledGPS()) {


            Intent intent = new Intent(MainActivity.this, MyService.class);
            startService(intent);
            Intent locationUpdateIntent = new Intent(MainActivity.this,
                    LocationUpdateService.class);
            startService(locationUpdateIntent);

        }
    }

    void gettingIMEI() {

        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        Log.d(TAG, "onRequestPermissionsResult: Got IMEI");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.READ_PHONE_STATE},2);

        }
        deviceID = telephonyManager.getDeviceId();
        Log.d(TAG, "onRequestPermissionsResult: Device Id " + deviceID);
        SharedPreferences sharedPreferences = getSharedPreferences("device_id", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id", deviceID);
        editor.apply();
//        if(!sharedPreferences.contains("id")) {
//
//            Log.d(TAG, "onRequestPermissionsResult: IMEI saved to Local DB ");
//        } else {
//
//            Log.d(TAG, "gettingIMEI: already exist in Local DB");
//        }


    }



    private void gettingIMEIPermission() {

        Log.d(TAG, "gettingIMEI: Getting IMEI request permission...");
        telephonyManager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        //telephonyManager.listen(new CustomPhoneStateListener(this), PhoneStateListener.LISTEN_CALL_STATE);



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {

                gotIMEI = true;

            Log.d(TAG, "gettingIMEI: Got IMEI...");

        }else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    2);
        }


    }

    private boolean EnabledGPS() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Log.d(TAG, "EnabledGPS: Enabling...");


        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            return true;
        } else {

            showGPSDisabledAlertToUser();
//
        }

        return false;
    }

    private void showGPSDisabledAlertToUser() {

        Log.d(TAG, "showGPSDisabledAlertToUser: alertdialog appear!");
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Go To Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alert = alertDialogBuilder.create();
        alert.show();
        gotGPSenabled = true;


    }


    private void getLocationPermission() {


        Log.d(TAG, "getLocationPermission: persmisson granted");

        String[] persmissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                permissonGranted = true;
                EnabledGPS();

            } else {

                ActivityCompat.requestPermissions(this, persmissions, 1);
            }
        } else {

            ActivityCompat.requestPermissions(this, persmissions, 1);
        }

    }

    @SuppressLint("HardwareIds")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case 1:

                if (grantResults.length > 0) {

                    for (int i = 0; i < grantResults.length; i++) {

                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {

                            permissonGranted = false;
                        }
                    }
                } else {

                    EnabledGPS();
                    permissonGranted = true;

                }

            case 2:
                if (grantResults.length > 0) {

                    for (int i = 0; i < grantResults.length; i++) {

                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {

                            gotIMEI = false;
                        } else {

                            gettingIMEI();
                        }
                    }
                    gettingIMEI();
                }

        }




    }

//
//    void locationChanging() {
//
//
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//
//
//
//            }
//
//            @Override
//            public void onStatusChanged(String s, int i, Bundle bundle) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String s) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String s) {
//
//            }
//        };
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            return;
//        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000,
//                0, locationListener);
//
//
//
//
//    }



}
