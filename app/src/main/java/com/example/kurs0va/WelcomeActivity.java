package com.example.kurs0va;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean isAuthenticated = preferences.getBoolean("isAuthenticated", false);
        if (isAuthenticated)
        {
            goMain();
        } else
        {
            setContentView(R.layout.welcome_activity);
        }
    }
    public void goMain()
    {
        Intent intent1 = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent1);
    }
    public void goReg(View v)
    {
      Intent intent1 = new Intent(WelcomeActivity.this, RegisterActivity.class);
      startActivity(intent1);
    }
    public void goLog(View v)
    {
        Intent intent2 = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(intent2);
    }

}
