package com.example.jevons.ward_1;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RunningActivity extends AppCompatActivity implements SensorEventListener {

    private Button button_countdown;
    private TextView text_timer, text_time;
    private Button button_stop;
    private TextView status;
    private EditText folder_name;

    long time, init, now;

    private TextView ax, ay, az, gx, gy, gz, mx, my, mz, cx, cy, cz;
    private Sensor aSensor, gSensor, mSensor, cSensor;
    private SensorManager asm, gsm, msm, csm;

    private String avalue= "";
    private String gvalue= "";
    private String mvalue= "";
    private String cvalue= "";
    private String foldername = "";

    ReceiverService receiverservice = new ReceiverService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);

        button_countdown = (Button) findViewById(R.id.button_countdown);
        text_timer = (TextView) findViewById(R.id.text_timer);
        button_stop = (Button)findViewById(R.id.button_stop);
        status = (TextView) findViewById(R.id.text_status);
        text_time = (TextView) findViewById(R.id.text_time);
        folder_name = (EditText) findViewById(R.id.folder_name);

        //accelerometer
        ax = (TextView) findViewById(R.id.text_x);
        ay = (TextView) findViewById(R.id.text_y);
        az = (TextView) findViewById(R.id.text_z);
        asm = (SensorManager)getSystemService(SENSOR_SERVICE);
        aSensor = asm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //gyroscope
        gx = (TextView) findViewById(R.id.text_gx);
        gy = (TextView) findViewById(R.id.text_gy);
        gz = (TextView) findViewById(R.id.text_gz);
        gsm = (SensorManager)getSystemService(SENSOR_SERVICE);
        gSensor = gsm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        //magnetometer
        mx = (TextView) findViewById(R.id.text_mx);
        my = (TextView) findViewById(R.id.text_my);
        mz = (TextView) findViewById(R.id.text_mz);
        msm = (SensorManager)getSystemService(SENSOR_SERVICE);
        mSensor = msm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        cx = (TextView) findViewById(R.id.text_cx);
        cy = (TextView) findViewById(R.id.text_cy);
        cz = (TextView) findViewById(R.id.text_cz);
        csm = (SensorManager)getSystemService(SENSOR_SERVICE);
        cSensor = csm.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        addListenerOnButtonStart();
    }

    float[] mGravity;
    float[] mGeomagnetic;
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor source = event.sensor;

        if (source.equals(aSensor)) {
            ax.setText("X: " + event.values[0]);
            ay.setText("Y: " + event.values[1]);
            az.setText("Z: " + event.values[2]);
            mGravity = event.values;
            avalue += event.values[0] + "\t" + event.values[1] + "\t" + event.values[2] + "\n";
        }
        if (source.equals(gSensor)) {
            gx.setText("X: " + event.values[0]);
            gy.setText("Y: " + event.values[1]);
            gz.setText("Z: " + event.values[2]);
            gvalue += event.values[0] + "\t" + event.values[1] + "\t" + event.values[2] + "\n";
        }
        if (source.equals(mSensor)) {
            mx.setText("X: " + event.values[0]);
            my.setText("Y: " + event.values[1]);
            mz.setText("Z: " + event.values[2]);
            mGeomagnetic = event.values;
            mvalue += event.values[0] + "\t" + event.values[1] + "\t" + event.values[2] + "\n";
        }
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                cx.setText("X: " + orientation[0]);
                cy.setText("Y: " + orientation[1]);
                cz.setText("Z: " + orientation[2]);
                cvalue += orientation[0] + "\t" + orientation[1] + "\t" + orientation[2] + "\n";
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    protected void AonResume() {
        super.onResume();
        asm.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_GAME);
        gsm.registerListener(this, gSensor, SensorManager.SENSOR_DELAY_GAME);
        msm.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }
    protected void AonPause() {
        super.onPause();
        asm.unregisterListener(this);
        gsm.unregisterListener(this);
        msm.unregisterListener(this);
    }

    public void addListenerOnButtonStart() {
        button_countdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //start everything
                text_timer.setText("Started");
                foldername = folder_name.getText().toString();
                startService(new Intent(getBaseContext(), Serv.class));
                init = System.currentTimeMillis();
                AonResume();
                    //stop everything
                    button_stop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            text_timer.setText("Stopped");
                            stopService(new Intent(getBaseContext(),Serv.class));
                            now = System.currentTimeMillis();
                            time = now - init;
                            text_time.setText(Long.toString(time));
                            AonPause();
                            try {
                                String h = DateFormat.format("MM-dd mm:ss", System.currentTimeMillis()).toString();
                                String ha = DateFormat.format("(1)MM-dd mm:ss", System.currentTimeMillis()).toString();
                                String hg = DateFormat.format("(2)MM-dd mm:ss:", System.currentTimeMillis()).toString();
                                String hm = DateFormat.format("(3)MM-dd mm:ss:", System.currentTimeMillis()).toString();
                                String hc = DateFormat.format("(4)MM-dd mm:ss:", System.currentTimeMillis()).toString();
                                String wha = DateFormat.format("(5)MM-dd mm:ss", System.currentTimeMillis()).toString();
                                String whg = DateFormat.format("(6)MM-dd mm:ss:", System.currentTimeMillis()).toString();
                                String whm = DateFormat.format("(7)MM-dd mm:ss:", System.currentTimeMillis()).toString();
                                String whc = DateFormat.format("(8)MM-dd mm:ss:", System.currentTimeMillis()).toString();

                                File root = new File(Environment.getExternalStorageDirectory(), "SensorData");
                                if(!root.exists()){
                                    root.mkdirs();
                                }

                                String newrootpath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SensorData";
                                File newroot = new File(newrootpath, foldername + " " + time + "(ms)");
                                if(!newroot.exists()){
                                    newroot.mkdirs();
                                }

                                File filepatha = new File(newroot, ha + ".txt");
                                File filepathg = new File(newroot, hg + ".txt");
                                File filepathm = new File(newroot, hm + ".txt");
                                File filepathc = new File(newroot, hc + ".txt");

                                File filepathwa = new File(newroot, wha + ".txt");
                                File filepathwg = new File(newroot, whg + ".txt");
                                File filepathwm = new File(newroot, whm + ".txt");
                                File filepathwc = new File(newroot, whc + ".txt");

                                FileWriter writera = new FileWriter(filepatha);
                                FileWriter writerg = new FileWriter(filepathg);
                                FileWriter writerm = new FileWriter(filepathm);
                                FileWriter writerc = new FileWriter(filepathc);

                                FileWriter writerwa = new FileWriter(filepathwa);
                                FileWriter writerwg = new FileWriter(filepathwg);
                                FileWriter writerwm = new FileWriter(filepathwm);
                                FileWriter writerwc = new FileWriter(filepathwc);

                                writera.append(avalue);
                                writerg.append(gvalue);
                                writerm.append(mvalue);
                                writerc.append(cvalue);

                                writerwa.append(receiverservice.valueA);
                                writerwg.append(receiverservice.valueG);
                                writerwm.append(receiverservice.valueM);
                                writerwc.append(receiverservice.valueC);

                                writera.flush();
                                writerg.flush();
                                writerm.flush();
                                writerc.flush();

                                writera.close();
                                writerg.close();
                                writerm.close();
                                writerc.close();

                                writerwa.flush();
                                writerwg.flush();
                                writerwm.flush();
                                writerwc.flush();

                                writerwa.close();
                                writerwg.close();
                                writerwm.close();
                                writerwc.close();

                                avalue = "";
                                gvalue = "";
                                mvalue = "";
                                cvalue = "";
                                receiverservice.valueA = "";
                                receiverservice.valueG = "";
                                receiverservice.valueM = "";
                                receiverservice.valueC = "";

                                status.setText("8 Files created in Folder: " + h + ".txt");
                            } catch (IOException e){
                                e.printStackTrace();
                                status.setText("FAIL");
                            }
                        }
                    });
            }
        });
    }
}

