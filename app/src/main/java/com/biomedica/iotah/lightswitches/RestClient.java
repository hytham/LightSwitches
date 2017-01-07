package com.biomedica.iotah.lightswitches;
import android.content.ContentValues;
import android.location.Address;
import android.os.AsyncTask;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.Log;


import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

/**
 * Created by haithem on 6/21/2016.
 */
public class RestClient  {


    public String POST(String JSON,String Address) throws ExecutionException, InterruptedException {
        return  new doPost().execute(JSON,Address).get();
    }
    public String GET(JSONObject JSON, String Address) throws ExecutionException, InterruptedException {

       return  new doGet().execute(JSON,Address).get();
    }
    private String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer).trim();
    }
    private  String getQueryString(JSONObject json) throws JSONException {
        StringBuilder sb = new StringBuilder();

        Iterator<String> keys = json.keys();
        sb.append("?"); //start of query args
        while (keys.hasNext()) {
            String key = keys.next();
            sb.append(key);
            sb.append("=");
            sb.append(json.get(key));
            sb.append("&"); //To allow for another argument.

        }

        return sb.toString();
    }



    protected class doGet extends AsyncTask<Object, String,String> {


        @Override
        protected String doInBackground(Object ... json) {

            JSONObject JSON=(JSONObject)json[0];
            String Address=(String)json[1];



            InputStream inputStream = null;



            String result = "";
            int len = 1500;
            try {
                URL url = new URL(Address+getQueryString(JSON));
                HttpURLConnection conn  = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type","application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.connect();



                int response = conn.getResponseCode();
                inputStream = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(inputStream, len);
                return contentAsString;

            } catch (Exception e) {
                Log.d("json","e.getLocalizedMessage()"+ e.getLocalizedMessage());
            }

            //  return result
            return result;

        }
    }

    protected  class doPost extends  AsyncTask<Object,String,String>{

        @Override
        protected String doInBackground(Object... json) {

            String JSON=(String) json[0];
            String Address=(String)json[1];

            InputStream inputStream = null;


            String result = "";
            int len = 1500;
            try {
                URL url = new URL(Address);
                HttpURLConnection conn  = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type","application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.connect();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(JSON);
                writer.close();
                os.close();

                int response = conn.getResponseCode();
                inputStream = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(inputStream, len);
                return contentAsString;

            } catch (Exception e) {
                Log.d("json","e.getLocalizedMessage()"+ e.getLocalizedMessage());
            }

            //  return result
            return result;
        }
    }


}



