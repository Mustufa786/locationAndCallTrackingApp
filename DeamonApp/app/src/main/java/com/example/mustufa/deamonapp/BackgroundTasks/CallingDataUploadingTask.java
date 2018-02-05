package com.example.mustufa.deamonapp.BackgroundTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

import static android.content.ContentValues.TAG;

/**
 * Created by Mustufa on 2/4/2018.
 */

public class CallingDataUploadingTask extends AsyncTask<String,Void,String> {


    private Context context;
    public CallingDataUploadingTask(Context cts) {

        this.context = cts;
    }

    @Override
    protected String doInBackground(String... strings) {
        String id = strings[0];
        String callingType = strings[1];
        String  number = strings[2];
        String loc_lat = strings[3];
        String loc_long = strings[4];
        String address = strings[5];
        String date = strings[6];


        Log.d(TAG, "doInBackground: Calling Data uploaded to database" + id +
                " " + loc_lat + " " + loc_long + " " + number + " " + callingType + " " +address);
        try {
            URL url = new URL("http://hivelet.com/rcm/email/testapi.php?action=AddCallLog");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            String data  = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8")
                    + "&" + URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(loc_lat, "UTF-8")
                    + "&" + URLEncoder.encode("longitude","UTF-8") + "=" + URLEncoder.encode(loc_long,"UTF-8")
                    + "&" + URLEncoder.encode("number","UTF-8") + "=" + URLEncoder.encode(number,"UTF-8")
                    + "&" + URLEncoder.encode("type","UTF-8") + "=" + URLEncoder.encode(callingType,"UTF-8")
                    + "&" + URLEncoder.encode("address","UTF-8") + "=" + URLEncoder.encode(address,"UTF-8")
                    + "&" + URLEncoder.encode("date_time","UTF-8") + "=" + URLEncoder.encode(date,"UTF-8");


            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            inputStream.close();

            Log.d(TAG, "doInBackground: call data uploaded to database..");


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
}
