package com.example.scan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Logo extends AppCompatActivity {

    private static int timeout=3000;
    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo);

        txt=(TextView)findViewById(R.id.logo);

        Animation animation= AnimationUtils.loadAnimation(Logo.this,R.anim.logoanim);
        txt.startAnimation(animation);

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(Logo.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },timeout);
    }
}
