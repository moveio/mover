package io.move.moveio.moveio;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private TextView osa_x;
    private TextView osa_y;
    private TextView osa_z;

    private float last_x;
    private float last_y;
    private float last_z;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        osa_x = (TextView) findViewById(R.id.osa_x);
        osa_y = (TextView) findViewById(R.id.osa_y);
        osa_z = (TextView) findViewById(R.id.osa_z);

        last_x = 0;
        last_y = 0;
        last_z = 0;

        String result = null;
        try {
            result  = new RetrieveFeedTask().execute("loool").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (result == null) {
            osa_x.setText("Not sent");
        } else {
            osa_x.setText(result);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float diff = last_x - event.values[0];

        if (diff > 6) {
            osa_x.setText("Gesture fajrrrrrrrrd");
        } else {
            //osa_x.setText("Hello World!");
        }

        last_x = event.values[0];
        last_y = event.values[1];
        last_z = event.values[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {

            HTTPBlablablah example = new HTTPBlablablah();
            String json = example.gestureJSON("left");
            String response;
            try {
                response = example.post("http://10.10.4.117:8080/request", json);
            } catch (IOException e) {
                return e.toString();
            }

            return response;
        }

        protected void onPostExecute(String response) {
        }
    }
}

class HTTPBlablablah {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    String gestureJSON(String gestureType) {
        String requestString = "{"
                + "\"meta\":\"" + gestureType
                + "\"}";
        return requestString;
    }
}