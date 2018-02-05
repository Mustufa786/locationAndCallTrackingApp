package com.example.mustufa.deamonapp.BackgroundTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import static android.content.ContentValues.TAG;

/**
 * Created by Mustufa on 1/30/2018.
 */

public class BackgroundTask extends AsyncTask<String,Void,String> {

    private Context ctx;

    public BackgroundTask(Context context) {

        this.ctx = context;
    }
    @Override
    protected String doInBackground(String... strings) {
        String id = strings[0];
        Log.d(TAG, "doInBackground: id " + id);
        String  loc_lat = strings[1];
        Log.d(TAG, "doInBackground: Latitude " + loc_lat);
        String loc_long = strings[2];
        Log.d(TAG, "doInBackground: Longitude " + loc_long );
        String address = strings[3];


        Log.d(TAG, "doInBackground: " + address);
        try {
            URL url = new URL("http://hivelet.com/rcm/email/testapi.php?action=AddUser");
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

            Log.d(TAG, "doInBackground: Uploaded To Database..");


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

        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }
}
