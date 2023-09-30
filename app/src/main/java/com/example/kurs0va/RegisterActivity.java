package com.example.kurs0va;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class RegisterActivity extends AppCompatActivity
{
    public String USER_LOGIN = "user";

    public Snackbar snackbar1,snackbar2;
    public EditText eml;
    public EditText pass;
    public EditText pass2;

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.imageButton3).setOnClickListener(view -> signIn());
    }
    public void goMain(View v)
    {
        Snackbar snackbar1 = Snackbar.make(v, "Ви не заповнили усі поля для реєстрації", Snackbar.LENGTH_LONG);
        Snackbar snackbar2 = Snackbar.make(v, "Паролі не співпадають!", Snackbar.LENGTH_LONG);

        snackbar1=ReadWrite.ChangeSnack(snackbar1, getApplicationContext());
        snackbar2=ReadWrite.ChangeSnack(snackbar2, getApplicationContext());

        eml = findViewById(R.id.editTextTextPersonName);
        pass = findViewById(R.id.editTextTextPassword);
        pass2 = findViewById(R.id.editTextTextPassword2);

        String email = eml.getText().toString();
        String password = pass.getText().toString();



        if(TextUtils.isEmpty(eml.getText().toString())
                || TextUtils.isEmpty(pass.getText().toString())
                || TextUtils.isEmpty(pass2.getText().toString()))
                {snackbar1.show();}
        else if(pass.getText().toString().equals(pass2.getText().toString())==false){snackbar2.show();}
        else
        {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if(pass.length()>=8) {
                                Log.d(TAG, "createUserWithEmail:success");
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean("isAuthenticated", true);
                                editor.apply();
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Snackbar snackbar = Snackbar.make(v, "Мінімальна довжина паролю - 8 символів", Snackbar.LENGTH_LONG);
                                snackbar=ReadWrite.ChangeSnack(snackbar, getApplicationContext());
                                snackbar.show();
                            }
                        }
                        else
                        {
                            Snackbar snackbar3 = Snackbar.make(v, "Користувач з даною електронною адресою вже зареєстрований", Snackbar.LENGTH_LONG);
                            snackbar3=ReadWrite.ChangeSnack(snackbar3, getApplicationContext());
                            snackbar3.show();
                        }
                    });
        }
    }

    public void goLog(View v)
    {
        Intent intent2 = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent2);
    }

    public void goWelcome(View v)
    {
        Intent intent2 = new Intent(RegisterActivity.this, WelcomeActivity.class);
        startActivity(intent2);
    }

    public void onBackPressed()
    {
        Intent intent = new Intent(RegisterActivity.this,WelcomeActivity.class);
        startActivity(intent);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {

                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        Log.d(TAG, "signInWithCredential:success");
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isAuthenticated", true);
                        editor.apply();
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else
                    {
                        Log.w(TAG, "loginGoogle:failure", task.getException());
                    }
                });
        }

    }
