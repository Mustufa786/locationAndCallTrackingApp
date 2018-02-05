package com.example.mustufa.deamonapp.Services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.mustufa.deamonapp.BackgroundTasks.BackgroundTask;
import com.example.mustufa.deamonapp.modal.CustomPhoneStateListener;
import com.example.mustufa.deamonapp.Activities.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MyService extends Service {


    private FusedLocationProviderClient mFusedLocationProviderclient;

    //Vars..
    public static String add;
    public static String locLong;
    public static String locLat;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onStartCommand: service started");
        MyDeviceLocation();
        return START_STICKY;
    }


    void sendtingToDB() {

        Log.d(TAG, "sendToDB: Sending to Db...");

        SharedPreferences sharedPreferences = getSharedPreferences("device_id", MODE_PRIVATE);
        if(sharedPreferences.contains("id")) {

            String user_id = sharedPreferences.getString("id","not found");
            Log.d(TAG, "sendingToDatabase: id found");
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute(user_id,locLat,locLong,add);

        }



    }

    private void geoCoder(double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(this);
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            Log.d(TAG, "geoCoder: IOException" + e.getMessage());
        }

        if (list.size() > 0) {

            Address address = list.get(0);
            add = address.getAddressLine(0);
            Log.d(TAG, "geoCoder: address" + address.toString());

        }


    }

    private void MyDeviceLocation() {

        Log.d(TAG, "MyDeviceLocation: finding your device..");

        if (MainActivity.permissonGranted) {

            mFusedLocationProviderclient = new FusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocationProviderclient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {

                        Log.d(TAG, "onSuccess: found your location" + location.getLatitude() + location.getLongitude());

                        Log.d(TAG, "onSuccess: " + location.toString());
                        locLong = String.valueOf(location.getLongitude());
                        locLat = String.valueOf(location.getLatitude());

                        Log.d(TAG, "onSuccess: String " + locLong + " " + locLat);

                        double loc_lat = location.getLatitude();
                        double loc_long = location.getLongitude();
                        geoCoder(loc_lat, loc_long);
                        sendtingToDB();

                    } else {
                        Log.d(TAG, "onSuccess: not found your location..");

                    }
                }
            });


        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onDestroy: Service Stopped");
    }



}
