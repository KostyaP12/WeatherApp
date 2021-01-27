package com.geekbrains.weatherapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import android.content.Context;

import javax.net.ssl.HttpsURLConnection;


public class RemoteFetch {

    private static final String OPEN_WEATHER_MAP_API =
            "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=";

    public static JSONObject getJSON(Context context, String city) {
        String url = String.format(OPEN_WEATHER_MAP_API, city) + context.getString(R.string.api_key);
        try {
            final URL uri = new URL(url);
            HttpsURLConnection urlConnection = null;
            try {
                urlConnection = (HttpsURLConnection) uri.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));
                String result = getLines(reader);
                JSONObject data = new JSONObject(result);
                if (data.getInt("cod") != 200) {
                    return null;
                }
                return data;
            } catch (Exception e) {
                return null;
            }
        }catch (MalformedURLException e){
            e.printStackTrace();
            return null;
        }
    }

    private static String getLines(BufferedReader reader) {
        StringBuilder rawData = new StringBuilder(1024);
        String tempVariable;

        while (true) {
            try {
                tempVariable = reader.readLine();
                if (tempVariable == null) break;
                rawData.append(tempVariable).append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rawData.toString();
    }
}
