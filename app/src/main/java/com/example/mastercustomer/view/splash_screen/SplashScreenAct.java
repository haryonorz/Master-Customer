package com.example.mastercustomer.view.splash_screen;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mastercustomer.R;
import com.example.mastercustomer.utility.SessionManager;

public class SplashScreenAct extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_splash_screen);

        sessionManager = new SessionManager(this);

        new Handler().postDelayed(() -> {
            sessionManager.checkLogin();
        }, 3000);
    }
}
