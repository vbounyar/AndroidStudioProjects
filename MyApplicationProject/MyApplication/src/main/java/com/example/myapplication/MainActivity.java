/*
    Sources Referenced:

    For User permission, GET http connection and converting input stream into string =>
        http://developer.android.com/training/basics/network-ops/connecting.html#download

    For establishing http connection from url and make URL object =>
        http://developer.android.com/reference/java/net/HttpURLConnection.html

     *Note that this example does not take into account that while getting the input
     stream that it may stall. No connection time out.*

     More information for POST (scroll to heading, "HTTP Methods") =>
     http://developer.android.com/reference/java/net/HttpURLConnection.html
 */


package com.example.myapplication;

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

public class MainActivity extends Activity {

    /*
        Variables to check to see if network is connected
     */

    private ConnectivityManager connectManager;
    private NetworkInfo networkInfo;
    private Context context;

    /*
        Interface stuff. EditText, urlText, is where user will type in a valid internet address;
        TextView, textView, is to show results if connection estabished, connection failed,
        the codes of the webpage and errors.
     */

    private EditText urlText;
    private TextView textView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up the EditText, TextView and Button
        urlText = (EditText)findViewById(R.id.editText);
        textView = (TextView)findViewById(R.id.textView);
        button = (Button)findViewById(R.id.button);

    }

    /*
        Method to check the connectivity. TextView shows if the network is connected or not.
        Note that this method is set to the button's onclick() in the xml.
     */
    public void checkConnectivity(View v) {

        //Grabs the string of the url from the editText, urlText;
        String stringUrl = urlText.getText().toString();

        connectManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //Get the network information
        networkInfo = connectManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            textView.setText("Network connection is available.");

            //Async stuff here
            new DownloadWebpageTask().execute(stringUrl);

        } else {

            textView.setText("No network connection available.");
        }

    }

    ///////////////PRIVATE CLASS - DownloadWebpageTask\\\\\\\\\\\\\\\\\\\\\
    private class DownloadWebpageTask extends AsyncTask<String /* Param */, Void /* Progress */, String /* Result */> {

        private HttpURLConnection urlConnection;
        private InputStream inputStream;

        //Limit of Characters to be displayed off of the downloaded webpage.
        private int lengthOfCharacters = 500;

        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);

            } catch (Exception e) {
                Log.i("ERROR", e.toString());
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results after downloading the webpage.
        // This result is limited to 500 characters.
        @Override
        protected void onPostExecute(String result) {
            textView.setText(result);
        }

        public String downloadUrl(String url) {
            String str = "If this is not changed, then connection didn't work.";

            try{

                URL myUrl = new URL(url);

                //Must up cast result to HttpURLConnection.
                //GET is default for urlConnection.
                urlConnection = (HttpURLConnection)myUrl.openConnection();

                inputStream = new BufferedInputStream(urlConnection.getInputStream());

                //Converted the webpage to strings. This is limited to 500 characters.
                str = readIt(inputStream, lengthOfCharacters);

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

        // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream, int len) throws IOException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }
    }


    ///////////////END OF PRIVATE CLASS\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    /* May erase the code below as menu is not used in this project.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    */
    
}
