package com.gattaca.bitalinoecgchart;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.gattaca.bitalinoecgchart.navigation.NaviagableActivity;

public class MainActivity extends NaviagableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MonitorActivity.class);
                startActivity(intent);
            }
        });

    }
}
