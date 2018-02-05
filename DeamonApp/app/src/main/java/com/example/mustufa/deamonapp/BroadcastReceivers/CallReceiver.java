package com.example.mustufa.deamonapp.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.mustufa.deamonapp.Activities.MainActivity;
import com.example.mustufa.deamonapp.BackgroundTasks.CallingDataUploadingTask;
import com.example.mustufa.deamonapp.Services.LocationUpdateService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class CallReceiver extends BroadcastReceiver {

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static String callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {

            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            Log.d(TAG, "onReceive: outgoing Number " + savedNumber);
        }else {

            String extraState = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            savedNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if(extraState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                state = TelephonyManager.CALL_STATE_RINGING;
            }else if (extraState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            }else if (extraState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                state = TelephonyManager.CALL_STATE_IDLE;
            }

            onCallStateChanged(context,state,savedNumber);
        }



    }

    private void onCallStateChanged(Context context, int state, String number) {

        if(lastState == state){
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date date = new Date();
                callStartTime = df.format(date);
                savedNumber = number;

                Toast.makeText(context, "Incoming Call Ringing" , Toast.LENGTH_SHORT).show();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if(lastState != TelephonyManager.CALL_STATE_RINGING){
                    isIncoming = false;
                    DateFormat df_ = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    Date date_ = new Date();
                    callStartTime = df_.format(date_);
                    Toast.makeText(context, "Outgoing Call Started" , Toast.LENGTH_SHORT).show();
                }

                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                    //Ring but no pickup-  a miss
                    Log.d(TAG, "onCallStateChanged: Missed Call " + savedNumber + "Call time" + callStartTime);
                    Toast.makeText(context, "Ringing but no pickup" + savedNumber + " Call time " + callStartTime +
                            " Date " + new Date() , Toast.LENGTH_SHORT).show();
                    CallingDataUploadingTask task = new CallingDataUploadingTask(context);
                    task.execute(MainActivity.deviceID,"Missed Call",savedNumber,
                            LocationUpdateService.lati,LocationUpdateService.longi,
                            LocationUpdateService.addr,callStartTime);
                }
                else if(isIncoming){

                    Log.d(TAG, "onCallStateChanged: incomming " + savedNumber + "Call Time" + callStartTime);
                    Toast.makeText(context, "Incoming " + savedNumber + " Call time " + callStartTime  , Toast.LENGTH_SHORT).show();
                    CallingDataUploadingTask task = new CallingDataUploadingTask(context);
                    task.execute(MainActivity.deviceID,"Incoming Call",savedNumber,
                            LocationUpdateService.lati,LocationUpdateService.longi,
                            LocationUpdateService.addr,callStartTime);
                }
                else{

                    Log.d(TAG, "onCallStateChanged: outgoing " + savedNumber + "Call Time " + callStartTime);
                    Toast.makeText(context, "outgoing " + savedNumber + " Call time "
                            + callStartTime +" Date " + new Date() , Toast.LENGTH_SHORT).show();
                    CallingDataUploadingTask task = new CallingDataUploadingTask(context);
                    task.execute(MainActivity.deviceID,"Outgoing Call",savedNumber,
                            LocationUpdateService.lati,LocationUpdateService.longi,
                            LocationUpdateService.addr,callStartTime);

                }

                break;
        }
        lastState = state;
    }


}
