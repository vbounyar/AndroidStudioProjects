package com.example.weatherdataexample;

/**
 * Created by be127-mac on 8/3/13.
 */

import java.util.List;

import com.google.gson.annotations.SerializedName;

/*
    This class is based on the JSON structure of wunderground.com
 */
public class WeatherData {
    @SerializedName("current_observation")
    public CurrentObservation currentObservation;


    public class CurrentObservation{

        @SerializedName("temperature_string")
        public String temperatureString;

         @SerializedName("temp_f")
         public String tempF;
    }

}

