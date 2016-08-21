package com.gattaca.bitalinoecgchartwithlibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.gattaca.bitalinoecgchartwithlibrary.navigation.NaviagableActivity;

public class MainActivity extends NaviagableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        toolbar =
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_main);

        setSupportActionBar(toolbar);
        onCreateToolbar(toolbar);

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
