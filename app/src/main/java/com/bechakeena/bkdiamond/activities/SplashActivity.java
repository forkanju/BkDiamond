package com.bechakeena.bkdiamond.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bechakeena.bkdiamond.R;
import com.bechakeena.bkdiamond.databinding.ActivitySplashBinding;
import com.bechakeena.bkdiamond.utils.SharedDataSaveLoad;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity{
    private ActivitySplashBinding binding = null;

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    private String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Animation animation = AnimationUtils.loadAnimation(this, R.anim.transition);
        token = SharedDataSaveLoad.load(SplashActivity.this, getString(R.string.preference_access_token));

        if (getIntent().getExtras() != null) {
            //custom notification implement here
        }

        //start animation
        binding.imgLogo.startAnimation(animation);
        binding.txtWelcome.startAnimation(animation);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                checkLogin();
            }
        }, SPLASH_TIME_OUT);
    }

    private void checkLogin(){
        if (!TextUtils.isEmpty(token)) goHome();
        else goLogin();
    }

    private void goHome(){
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void goLogin(){
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
