package com.example.jevons.ward_1;

/**
 * Created by Jevons on 6/8/2017.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class Serv extends Service {

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Toast.makeText(this, "Starting...", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Stopping...", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }
}
