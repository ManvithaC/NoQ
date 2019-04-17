package com.example.noq;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

public class DownloadUrl {

    private static int responseCode = 0;

    public String readUrl(String strUrl) throws IOException {
        Log.d("url", "***********");
        Log.d("url", strUrl);
        String data = "";

        OutputStream oStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            responseCode = urlConnection.getResponseCode();

            if(responseCode == 200){
                // Reading data from url
                InputStream iStream = null;
                iStream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                StringBuffer sb = new StringBuffer();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();
                Log.d("downloadUrl", data.toString());

                br.close();
                iStream.close();
            }

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            urlConnection.disconnect();
        }
        return data;
    }

    public static HashMap<String, String> jsonToMap(String t) throws JSONException {
        HashMap<String, String> map = new HashMap<String, String>();
        JSONObject jObject = new JSONObject(t);
        Iterator<?> keys = jObject.keys();

        while( keys.hasNext() ){
            String key = (String)keys.next();
            String value = jObject.getString(key);
            map.put(key, value);
        }

        return map;
    }

    public static int getResponseCode() {
        return responseCode;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        Log.d("stringify payload", new JSONObject(params).toString());
        return new JSONObject(params).toString();
    }
}