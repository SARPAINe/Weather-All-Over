package com.example.weatherallover;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText inputCity;
    String cityName="";
    DownloadTask task;
    TextView main;
    public void getInputCity(View view){
        cityName=inputCity.getText().toString();
        if(cityName.length()!=0)
        {
            task=new DownloadTask();
            try{

            task.execute("http://api.openweathermap.org/data/2.5/weather?q="+cityName+"&APPID=f8e1d7a38872f9e8b2b5a477225e3769");
            }
            catch (Exception e){
                Toast.makeText(this, "There seems to be an error! Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
//            Log.i("city info",cityName);
        }
        else
        {
            main.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "City name can not be empty!", Toast.LENGTH_SHORT).show();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection httpURLConnection;
            try {
                url=new URL(urls[0]);
                httpURLConnection= (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                InputStream in=httpURLConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while (data!=-1){
                    char current= (char) data;
                    result+=current;
                    data=reader.read();
                }
                return result;
            }
            catch (Exception e){
                e.printStackTrace();
                return "failed!";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                String cod=jsonObject.getString("cod");
                if(cod.equals("404"))
                {
                    main.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "City Not found! Please Enter a Valid City.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String weather=jsonObject.getString("weather");
//                Log.i("JSON weather",weather);
                    JSONArray arr=new JSONArray(weather);

                    for(int i=0;i<arr.length();i++)
                    {
                        JSONObject jsonPart=arr.getJSONObject(i);
//                        Log.i("main",jsonPart.getString("main"));
//                        Log.i("description",jsonPart.getString("description"));
                        main.setText(jsonPart.getString("main")+": "+jsonPart.getString("description"));
                        main.setVisibility(View.VISIBLE);
                    }
                }



            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "City Not found! Please Enter a Valid City or Check your Internet Connection.", Toast.LENGTH_SHORT).show();
                main.setVisibility(View.INVISIBLE);

            }

//
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputCity=(EditText)findViewById(R.id.inputCity);
        main=(TextView)findViewById(R.id.mainText);
//        Log.i("city name":"")

    }
}
