package com.example.noq;
import android.util.Log;

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
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class PostUrl {

    private static int responseCode = 0;

    public String postData(HashMap<String, String> newUser, String strUrl) throws IOException {
        String data = "";
        OutputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");

            urlConnection.setRequestProperty("content-type", "application/json; charset=utf-8");

            iStream = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(iStream, "UTF-8"));

            StringBuffer sb = new StringBuffer();
            writer.write(getPostDataString(newUser));

            writer.flush();
            writer.close();

            responseCode=urlConnection.getResponseCode();
            Log.d("responseCode post call", String.valueOf(responseCode));

            if (responseCode == HttpsURLConnection.HTTP_CREATED) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line=br.readLine()) != null) {
                    data+=line;
                }
            }
            else {
                data="";

            }

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        Log.d("response from post", data);
        return data;
    }

    public static int getResponseCode() {
        return responseCode;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        Log.d("stringify payload", new JSONObject(params).toString());
        return new JSONObject(params).toString();
    }
}