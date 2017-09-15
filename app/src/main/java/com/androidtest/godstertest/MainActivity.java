package com.androidtest.godstertest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private TextView shake_counter;
    private Button restartButton;

    TextView responseTv;
    TextView forecastTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        shake_counter = (TextView) findViewById(R.id.shake_counter);
        shake_counter.setText("0");
        restartButton = (Button) findViewById(R.id.restart_button);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shake_counter.setText("0");
            }
        });

        responseTv = (TextView) findViewById(R.id.response_tv);
        forecastTv = (TextView) findViewById(R.id.forecast_tv);
        String url = "http://api.openweathermap.org/data/2.5/weather?id=3530597&&appid=1feeb8fbf5d76cf1e71d1bff8ec374a3";
        /*{
            "id": 3530597,
                    "name": "Mexico City",
                    "country": "MX",
                    "coord": {
                "lon": -99.127663,
                        "lat": 19.428471
            }
        };*/
        /*{
        "coord":{
        "lon":-99.13,
        "lat":19.43
        },
        "weather":
        [{
        "id":800,
        "main":"Clear",
        "description":"clear sky",
        "icon":"02n"
        }],
        "base":"stations",
        "main":{
        "temp":285.71,
        "pressure":1027,
        "humidity":67,
        "temp_min":283.15,
        "temp_max":289.15
        },
        "visibility":16093,
        "wind":{
        "speed":1.5,
        "deg":70
        },
        "clouds":{
        "all":5
        },
        "dt":1505450880,
        "sys":{
        "type":1,
        "id":3998,
        "message":0.0036,
        "country":"MX",
        "sunrise":1505478250,
        "sunset":1505522311
        },
        "id":3530597,
        "name":"Mexico City",
        "cod":200
        }*/

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String temp_max = String.valueOf(response.getJSONObject("main").getDouble("temp_max") - 273.15);
                            String temp_min = String.valueOf(response.getJSONObject("main").getDouble("temp_min") - 273.15);
                            responseTv.setText("Max Temp: ".concat(temp_max).concat(" Min Temp: ".concat(temp_min)));
                            forecastTv.setText("Clima: ".concat(response.getJSONArray("weather").getJSONObject(0).getString("description")));
                        }catch(JSONException ex){
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        responseTv.setText("Error: " + error.getMessage());
                    }
                });

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            if(mAccel > 10){
                shake_counter.setText(String.valueOf(Integer.parseInt((String)shake_counter.getText()) + 1));
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }
}//http://api.openweathermap.org/