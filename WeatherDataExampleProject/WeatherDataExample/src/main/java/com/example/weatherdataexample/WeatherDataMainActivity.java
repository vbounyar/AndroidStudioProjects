package com.example.weatherdataexample;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;

public class WeatherDataMainActivity extends Activity {

    private ConnectivityManager connectManager;
    private NetworkInfo networkInfo;

    private EditText cityEditText;
    private TextView weatherTextView;
    private Button button;

    /*
    //Just in case want to do a city in California
    private String weatherUrlWithKeyUS = "http://api.wunderground.com/api/[YOUR KEY]/conditions/q/CA/";
    */

    //Works for any major city in England.
    //Get your key at http://www.wunderground.com/weather/api/d/pricing.html

    private String weatherUrlWithKeyEngland = "http://api.wunderground.com/api/[YOUR KEY]/conditions/forecast/q/England/";
    private String cityString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_data_activity_main);

        //Set up the EditText, TextView and Button
        cityEditText = (EditText)findViewById(R.id.editText);
        weatherTextView = (TextView)findViewById(R.id.textView);
        button = (Button)findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Grabs the string of the url from the editText, urlText;
                cityString =  cityEditText.getText().toString();

                //If cityString has spaces, replace with underscore character.
                if(cityString.contains(" ")){
                    cityString = cityString.replaceAll(" ", "_");
                }


                String stringUrl = weatherUrlWithKeyEngland + cityString + ".json";

                connectManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                //Get the network information
                networkInfo = connectManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {

                    weatherTextView.setText("Network connection is available.");

                    //Custom Async Class here
                    new DownloadWeatherDataTask().execute(stringUrl);

                } else {

                    weatherTextView.setText("No network connection available.");
                }

            }
        });
    }

    ///////////////PRIVATE CLASS - DownloadWeatherDataTask\\\\\\\\\\\\\\\\\\\\\
    private class DownloadWeatherDataTask extends AsyncTask<String /* Param */, Void /* Progress */, String /* Result */> {

        private HttpURLConnection urlConnection;
        private InputStream inputStream;

        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                String returnStr = downloadUrl(urls[0]);

                return returnStr;

            } catch (Exception e) {
                Log.i("ERROR", e.toString());
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results after downloading the webpage.
        @Override
        protected void onPostExecute(String result) {
            String message = "The " + cityString + " is " + result;
            weatherTextView.setText(message);
        }

        public String downloadUrl(String url) {
            String str = "If this is not changed, then connection didn't work.";

            Gson gson = new Gson();

            WeatherData weatherData = new WeatherData();

            try{

                URL myUrl = new URL(url);

                //Must up cast result to HttpURLConnection.
                //GET is default for urlConnection.
                urlConnection = (HttpURLConnection)myUrl.openConnection();

                //Get the stream off of the urlConnect
                inputStream = new BufferedInputStream(urlConnection.getInputStream());

                Reader reader = new InputStreamReader(inputStream);

                //From the json make a WeatherData object
                weatherData = gson.fromJson(reader, WeatherData.class);

                //Converted the webpage to strings.
                str = weatherData.currentObservation.temperatureString;

            } catch (Exception e) {
                Log.i("ERROR2", e.toString());
            } finally {

                //Disconnect urlConnection when done.
                urlConnection.disconnect();

                //Close inputStream when done
                if (inputStream != null) {

                    //There might be problems closing the input stream so wrap with try-catch.
                    try{
                        inputStream.close();
                    } catch(IOException ex){
                        Log.i("ERROR3", ex.toString());
                    }

                }
            }

            return str;
        }

    }

    ///////////////END OF PRIVATE CLASS\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
}
