package com.example.mustufa.deamonapp.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class LocationUpdateService extends Service {

    private LocationManager locationManager;
    public static String addr;
    public static double latitude,longitude;
    public static String lati,longi;

    public LocationUpdateService() {
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)

    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private class LocationListener implements android.location.LocationListener {

        Location mLastLocation;

        public LocationListener(String provider) {

            mLastLocation = new Location(provider);
        }


        @Override
        public void onLocationChanged(Location location) {

            mLastLocation.set(location);
            Log.d(TAG, "onLocationChanged: location is changing.." + location.toString());
            lati = String.valueOf(mLastLocation.getLatitude());
            longi = String.valueOf(mLastLocation.getLongitude());
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            geoCoder(latitude,longitude);
            SharedPreferences sharedPreferences = getSharedPreferences("device_id", MODE_PRIVATE);
            if (sharedPreferences.contains("id")) {
                String device_id = sharedPreferences.getString("id", "not found");
                LocationUpdateTask task = new LocationUpdateTask(this);
                task.execute(device_id, lati, longi,addr);
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

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
            addr = address.getAddressLine(0);
            Log.d(TAG, "geoCoder: address" + address.toString());

        }


    }


    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 600000, 0,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 600000, 0,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (locationManager == null) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private class LocationUpdateTask extends AsyncTask<String, Void, String>{

        android.location.LocationListener locationListener;

        LocationUpdateTask(android.location.LocationListener locationListener) {

            this.locationListener = locationListener;

        }

        @Override
        protected String doInBackground(String... strings) {
            String id = strings[0];
            String loc_lat = strings[1];
            String  loc_long = strings[2];
            String address = strings[3];



            Log.d(TAG, "doInBackground: " + id + " " + loc_lat + " " + loc_long + " " + address);
            try {
                URL url = new URL("http://hivelet.com/rcm/email/testapi.php?action=UpdateUser");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String data  = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
                        + "&" + URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(loc_lat, "UTF-8")
                        + "&" + URLEncoder.encode("longitude","UTF-8") + "=" + URLEncoder.encode(loc_long,"UTF-8")
                        + "&" + URLEncoder.encode("address","UTF-8") + "=" + URLEncoder.encode(address,"UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                inputStream.close();

                Log.d(TAG, "doInBackground: location changes Uploaded To Database..");


                return "Successfully Uploaded data";



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Toast.makeText(LocationUpdateService.this, s, Toast.LENGTH_SHORT).show();
        }
    }
}
