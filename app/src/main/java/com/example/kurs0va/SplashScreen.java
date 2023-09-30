package com.example.kurs0va;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;


public class SplashScreen extends AppCompatActivity
{
    private final int SPLASH_DISPLAY_LEHGHT=2500;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro1);
        getWindow().
                getDecorView().
                setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                Intent mainIntent = new Intent(SplashScreen.this, WelcomeActivity.class);;
                if(isInternetAvailable()){SplashScreen.this.startActivity(mainIntent);SplashScreen.this.finish();}
                else {
                    Snackbar snackbar= Snackbar.make(findViewById(android.R.id.content), "Немає підключення інтернету! Перевірте з'єднання", Snackbar.LENGTH_LONG);
                    snackbar=ReadWrite.ChangeSnack(snackbar, getApplicationContext());
                    snackbar.show();}
            }
        },SPLASH_DISPLAY_LEHGHT);
    }

    public void onBackPressed()
    {
        super.onBackPressed();
    }

    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
