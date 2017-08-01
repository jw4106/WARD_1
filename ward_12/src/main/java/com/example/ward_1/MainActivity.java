package com.example.ward_1;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.wearable.view.WatchViewStub;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends Activity implements SensorEventListener {

    private TextView text_status, texta, textg, ax, ay, az, gx, gy, gz;
    private Button button_start, button_stop;
    private SensorManager asm, gsm;
    private Sensor aSensor, gSensor;

    private String avalue = "";
    private String gvalue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override public void onLayoutInflated(WatchViewStub stub) {

                ax = (TextView)findViewById(R.id.ax);
                ay = (TextView)findViewById(R.id.ay);
                az = (TextView)findViewById(R.id.az);
                asm = (SensorManager)getSystemService(SENSOR_SERVICE);
                aSensor = asm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

                gx = (TextView)findViewById(R.id.gx);
                gy = (TextView)findViewById(R.id.gy);
                gz = (TextView)findViewById(R.id.gz);
                gsm = (SensorManager)getSystemService(SENSOR_SERVICE);
                gSensor = gsm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

                text_status = (TextView)findViewById(R.id.text_status);
                button_start = (Button)findViewById(R.id.button_start);
                button_stop = (Button)findViewById(R.id.button_stop);
                addListenerOnButtonStart();
            }
        });
    }

        @Override
        public void onSensorChanged(SensorEvent event) {
            Sensor source = event.sensor;

            if (source.equals(aSensor)) {
                ax.setText("X: " + event.values[0]);
                ay.setText("Y: " + event.values[1]);
                az.setText("Z: " + event.values[2]);
                avalue += event.values[0] + "\t" + event.values[1] + "\t" + event.values[2] + "\n";
            }
            if (source.equals(gSensor)) {
                gx.setText("X: " + event.values[0]);
                gy.setText("Y: " + event.values[1]);
                gz.setText("Z: " + event.values[2]);
                gvalue += event.values[0] + "\t" + event.values[1] + "\t" + event.values[2] + "\n";
            }
        }

        public String getavalue(){
            return avalue;
        }

        public String getgvalue(){
            return gvalue;
        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        protected void AonResume() {
            super.onResume();
            asm.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_GAME);
            gsm.registerListener(this, gSensor, SensorManager.SENSOR_DELAY_GAME);
        }
        protected void AonPause() {
            super.onPause();
            asm.unregisterListener(this);
            gsm.unregisterListener(this);
        }

    public void addListenerOnButtonStart(){
        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                text_status.setText("Started");
                AonResume();

                button_stop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        text_status.setText("Stopped");
                        AonPause();
                        try {
                            String h = DateFormat.format("MM-dd mm:ss", System.currentTimeMillis()).toString();
                            String ha = DateFormat.format("(5)MM-dd mm:ss", System.currentTimeMillis()).toString();
                            String hg = DateFormat.format("(6)MM-dd mm:ss:", System.currentTimeMillis()).toString();

                            File root = new File(Environment.getExternalStorageDirectory(), "SensorData");
                            if(!root.exists()){
                                root.mkdirs();
                            }

                            String newrootpath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SensorData";
                            File newroot = new File(newrootpath, h);
                            if(!newroot.exists()){
                                newroot.mkdirs();
                            }

                            File filepatha = new File(newroot, ha + ".txt");
                            File filepathg = new File(newroot, hg + ".txt");

                            FileWriter writera = new FileWriter(filepatha);
                            FileWriter writerg = new FileWriter(filepathg);

                            writera.append(avalue);
                            writerg.append(gvalue);

                            writera.flush();
                            writerg.flush();

                            writera.close();
                            writerg.close();

                            avalue = "";
                            gvalue = "";
                            text_status.setText("4 Files created in Folder: " + h + ".txt");
                        } catch (IOException e){
                            e.printStackTrace();
                            text_status.setText("FAIL");
                        }
                    }
                });
            }
        });
    }
}
