package com.geekbrains.weatherapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.util.Locale;

import javax.crypto.spec.PSource;

public class WeatherFragment extends Fragment {
    TextView cityField;
    TextView currentTemperatureField;
    TextView detailsField;
    ImageView weatherIcon;

    public WeatherFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        updateWeatherData(new CityPreference(getActivity()).getCity());
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        cityField = (TextView) view.findViewById(R.id.cityField);
        currentTemperatureField = (TextView) view.findViewById(R.id.currentTemperatureField);
        detailsField = (TextView) view.findViewById(R.id.detailsField);
        weatherIcon = (ImageView) view.findViewById(R.id.weatherIcon);
    }

    public void changeCity(String city) {

        updateWeatherData(city);
    }

    private void updateWeatherData(final String city) {
        final Handler handler = new Handler(Looper.getMainLooper());
        new Thread() {
            public void run() {
                final JSONObject json = RemoteFetch.getJSON(getActivity(), city);
                if (json == null) {

                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.city_not_found),
                                    Toast.LENGTH_LONG).show();
                            Toast.makeText(getActivity(), city, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            renderWeather(json);
                        }

                    });
                }
            }
        }.start();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void renderWeather(JSONObject json) {
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + main.getString("humidity") + "%" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa");

            currentTemperatureField.setText(
                    String.format("%.1f", main.getDouble("temp")) + " ℃");

        } catch (Exception e) {
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }


}
