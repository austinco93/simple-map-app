package com.example.shakabreaux.googlemapstest2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class IntroActivity extends AppCompatActivity {
    private Button start;
    private Button instructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        start = (Button) findViewById(R.id.startBtn);
        instructions = (Button) findViewById(R.id.insBtn);

        start.setOnClickListener(mClickListener);
        instructions.setOnClickListener(mClickListener);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == start) {
                startActivity(new Intent(IntroActivity.this, MapsActivity.class));
            } else {
                startActivity(new Intent(IntroActivity.this, InstructionsActivity.class));
            }
        }
    };

}
