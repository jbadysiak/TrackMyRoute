package com.example.jakubbadysiak.trackmyroute;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jakubbadysiak.trackmyroute.Accelerometer.StepDetector;
import com.example.jakubbadysiak.trackmyroute.Accelerometer.StepListener;
import com.example.jakubbadysiak.trackmyroute.GSM.MyPhoneListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener, StepListener{

    public static final String EXTRA_MESSAGE = "com.example.jakubbadysiak.MESSAGE";
    private Intent intentMessage;
    private Intent choosenIntent;
    private String chooserTitle;
    private Intent intentMap;
    private StepDetector stepDetector;
    private SensorManager sensorManager;
    private BatteryManager batteryManager;
    private int batLevel;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of steps: ";
    private int numSteps;
    private TextView tvSteps;
    private Button buttonStart;
    private Button buttonStop;
    private Button buttonReset;
    private Button buttonMap;
    private TextView tvAccel;
    private String phoneDetails;
    private float x;
    private float y;
    private float z;
    private TelephonyManager telephonyManager;
    private MyPhoneListener myPhoneListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        chooserTitle = getString(R.string.chooser);
        batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        stepDetector = new StepDetector();
        stepDetector.registerListener(this);
        myPhoneListener = new MyPhoneListener();
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(myPhoneListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        //intentMessage = new Intent(this, MessageActivity.class);
        intentMap = new Intent(this, MapsActivity.class);
        intentMessage = new Intent(Intent.ACTION_SEND);
        choosenIntent = Intent.createChooser(intentMessage, chooserTitle);


        tvSteps = (TextView) findViewById(R.id.tvSteps);
        tvAccel = (TextView) findViewById(R.id.tvAccel);
        buttonStart = (Button) findViewById(R.id.btnStart);
        buttonStop = (Button) findViewById(R.id.btnStop);
        buttonReset = (Button) findViewById(R.id.btnReset);
        buttonMap = (Button) findViewById(R.id.btnMap);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numSteps = 0;
                sensorManager.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sensorManager.unregisterListener(MainActivity.this);
                batLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                phoneDetails = TEXT_NUM_STEPS + numSteps +"\nAccelerator: \nX = "+ x +"  Y = "+ y +"  Z = "+ z +"\nBattery = " +batLevel+ "%\nSignal GSM = " +myPhoneListener.signalStrengthValue+ "dB";

                try{
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    String currentDateAndTime = sdf.format(new Date());
                    File root = new File(Environment.getExternalStorageDirectory(), "Notes");
                    if (!root.exists()){
                        root.mkdirs();
                    }
                    File file = new File(root, currentDateAndTime + ".txt");
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.append(phoneDetails.toString());
                    fileWriter.flush();
                    fileWriter.close();
                }catch (IOException e){
                    e.printStackTrace();
                }

                String message = phoneDetails.toString();

                //intentMessage.putExtra(EXTRA_MESSAGE, message);
                //startActivity(intentMessage);
                intentMessage.setType("text/plain");
                intentMessage.putExtra(Intent.EXTRA_TEXT, message);
                //startActivity(intentMessage);
                startActivity(choosenIntent);


            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numSteps = 0;
                tvSteps.setText(TEXT_NUM_STEPS + numSteps);
            }
        });

        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentMap);
            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            stepDetector.updateAccel(
                    sensorEvent.timestamp, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]
            );
            x = sensorEvent.values[0];
            y = sensorEvent.values[1];
            z = sensorEvent.values[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void step(long timeNS) {
        numSteps++;
        tvSteps.setText(TEXT_NUM_STEPS + numSteps);
    }
}
