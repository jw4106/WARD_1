package com.example.jevons.ward_1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public Switch switch1, switch2, switch3, switch4;
    private Button button_s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addListenerOnButtonStart();
    }

    public void addListenerOnButtonStart(){
        switch1 = (Switch)findViewById(R.id.switch_a);
        switch2 = (Switch)findViewById(R.id.switch_g);
        switch3 = (Switch)findViewById(R.id.switch_m);
        switch4 = (Switch)findViewById(R.id.switch_c);
        button_s = (Button)findViewById(R.id.button_start);

        button_s.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, RunningActivity.class);
                startActivity(intent);
                }
        }
        );
    }


}
