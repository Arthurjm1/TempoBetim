package com.example.usuario.tempobetim;


import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView mLocalTextView;
    TextView mConditionTextView;
    TextView mHumidityTextView;
    TextView mWindVelocityTextView;
    TextView mTemperatureTextView;
    URL url;
    String[] response;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocalTextView = (TextView) findViewById(R.id.tv_local);
        mConditionTextView = (TextView) findViewById(R.id.tv_condition);
        mHumidityTextView = (TextView) findViewById(R.id.tv_humidity);
        mWindVelocityTextView = (TextView) findViewById(R.id.tv_wind_velocity);
        mTemperatureTextView = (TextView) findViewById(R.id.tv_temperature);

        try{
            url = new URL("http://apiadvisor.climatempo.com.br/api/v1/weather/locale/6880/current?token=a47404cabfe5ceeae8786bca743b3d0c");
        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        new WeatherQueryTask().execute(url);

    }

    class WeatherQueryTask extends AsyncTask<URL, Void, String[]>{

        @Override
        protected String[] doInBackground(URL... urls) {
            URL url = urls[0];
            try{
                response = getResponse(url);
            }catch (IOException e){
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            mLocalTextView.setText(strings[0] + "-" + strings[1]);
            mConditionTextView.setText("Condição: " + strings[2]);
            mHumidityTextView.setText("Humidade: " + strings[3] + "%");
            mWindVelocityTextView.setText("Velocidade do Vento: " + strings[4] + "Km/h");
            mTemperatureTextView.setText(strings[5]+"ºC");

        }
    }

    public String[] getResponse(URL url) throws IOException {
        String[] jsonStrings = new String[6];
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");

            JsonReader jsonReader = new JsonReader(inputStreamReader);
            jsonReader.beginObject();
            while(jsonReader.hasNext()){
                String key = jsonReader.nextName();
                if(key.equals("name")){
                    jsonStrings[0] = jsonReader.nextString();
                }else if(key.equals("state")){
                    jsonStrings[1] = jsonReader.nextString();
                }else if(key.equals("data")){
                    jsonReader.beginObject();
                    while(jsonReader.hasNext()){
                        key = jsonReader.nextName();
                        if(key.equals("condition")){
                            jsonStrings[2] = jsonReader.nextString();
                        }else if(key.equals("humidity")){
                            jsonStrings[3] = String.valueOf(jsonReader.nextDouble());
                        }else if(key.equals("wind_velocity")){
                            jsonStrings[4] = String.valueOf(jsonReader.nextDouble());
                        }else if(key.equals("temperature")){
                            jsonStrings[5] = String.valueOf(jsonReader.nextInt());
                        }else{
                            jsonReader.skipValue();
                        }
                    }
                }
                else{
                    jsonReader.skipValue();
                }
            }
            return jsonStrings;
        } finally {
            httpURLConnection.disconnect();
        }

    }
}
