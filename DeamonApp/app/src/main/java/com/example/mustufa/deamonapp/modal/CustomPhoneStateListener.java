package com.example.mustufa.deamonapp.modal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Handler;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.mustufa.deamonapp.Activities.MainActivity;
import com.example.mustufa.deamonapp.BackgroundTasks.CallingDataUploadingTask;
import com.example.mustufa.deamonapp.Services.LocationUpdateService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


import static android.content.ContentValues.TAG;

/**
 * Created by Mustufa on 1/31/2018.
 */

public class CustomPhoneStateListener extends PhoneStateListener {

    private static String number;
    public static String formattedDate;
    private static String callStatus;
    Context context;
    private boolean onCall;
    private int millisconds;
    private boolean callDisconnected;


    public CustomPhoneStateListener(Context context) {

        this.context = context;
    }


    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);



        switch (state) {

            case TelephonyManager.CALL_STATE_RINGING:

                Log.d(TAG, "onCallStateChanged: Call is Ringing..");

                break;

            case TelephonyManager.CALL_STATE_OFFHOOK:
                callStatus = "Incomming Call";
                onCall = true;
                //callTimer();
                number = incomingNumber;
                Log.d(TAG, "onCallStateChanged: On Call.." + " Number: " + incomingNumber);
                Log.d(TAG, "onCallStateChanged: " + callStatus);
                break;


            case TelephonyManager.CALL_STATE_IDLE:

                callDisconnected = true;
                number = incomingNumber;
                callStatus = "Incomming Call";
                onCall = false;
                Log.d(TAG, "onCallStateChanged: Disconnected.....");
                Log.d(TAG, "onCallStateChanged: Calling Location " +
                LocationUpdateService.lati + " " + LocationUpdateService.longi + " " +
                        LocationUpdateService.addr + number);


                Log.d(TAG, "onCallStateChanged: Outgoing Call data Uploading to database");


                break;
        }

        callDisconnect();
    }

    void callDisconnect() {

        if(callDisconnected) {

            CallingDataUploadingTask task = new CallingDataUploadingTask(context);
            task.execute(MainActivity.deviceID,callStatus,number,LocationUpdateService.lati,
                    LocationUpdateService.longi,LocationUpdateService.addr);
        }
    }

//    private void callTimer() {
//
//        final Handler handler = new Handler();
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                int hours = (millisconds / (1000 * 60 * 60) %24);
//                int minutes = (millisconds / (1000 * 60)%60);
//                int seconds = (millisconds / (100)%60);
//                int milli = millisconds % 100;
//                String format = String.format("%02d:%02d:%02d:%02d",hours,minutes,seconds,milli);
//                Log.d(TAG, "run: on call " + format);
//                if(onCall) {
//                    millisconds++;
//                }else {
//
//
//                    Log.d(TAG, "run: " + format);
//                }
//                handler.postDelayed(this,1);
//            }
//        });
//
//    }



}
