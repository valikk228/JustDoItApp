package com.example.kurs0va;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity
{
    int count = 0;
    int count_completed = 0;
    ImageView imageView;
    Dialog dialog;
    Bitmap bitmap;
    Uri imageUri;
    String oldPass;
    String newPass;
    FirebaseUser currentUser;
    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        String userEmail = currentUser.getEmail();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tasks");
        Query query = databaseReference.orderByChild("email").equalTo(userEmail);
        TextView mail = findViewById(R.id.textView38);

        if(bitmap!=null && imageUri!=null)
        {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView = findViewById(R.id.imageView5);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        if (currentUser != null)
        {
            for (UserInfo profile : currentUser.getProviderData())
            {
                String providerId = profile.getProviderId();
                TextView password = findViewById(R.id.textView34);
                TextView photo = findViewById(R.id.textView36);
                // providerId может быть "google.com", "facebook.com", "password", и т.д.
                if(providerId.equals("google.com"))
                {
                    password.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            View view1 = findViewById(android.R.id.content);
                            Snackbar snackbar = Snackbar.make(view1,"Ви не можете змінити пароль, тому що ви увійшли через Google",Snackbar.LENGTH_LONG);
                            snackbar = ReadWrite.ChangeSnack(snackbar, getApplicationContext());
                            snackbar.show();
                        }
                    });
                }
                if(providerId.equals("password"))
                {
                    password.setOnClickListener(new View.OnClickListener() {
                        @Override
                         public void onClick(View view)
                        {
                            dialog=new Dialog(ProfileActivity.this);
                            dialog.setContentView(R.layout.password_dialog);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(363636));
                            dialog.setCancelable(true);
                            dialog.show();
                            Button editPassword = dialog.findViewById(R.id.editPassword);
                            Button backPassword = dialog.findViewById(R.id.back_pass);
                            EditText oldPassword = dialog.findViewById(R.id.oldPass);
                            EditText newPassword = dialog.findViewById(R.id.newPass);

                            backPassword.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.cancel();
                                }
                            });

                            editPassword.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if( oldPassword.getText().toString().equals("")==false)
                                    {oldPass = oldPassword.getText().toString();}
                                    if(oldPass==null)oldPass="1";
                                    if( newPassword.getText().toString().equals("")==false)
                                    {newPass = newPassword.getText().toString();}
                                    if(newPass==null)newPass="1";

                                    AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), oldPass);

                                    currentUser.reauthenticate(credential)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User re-authenticated successfully.");
                                                    if(newPass.length()>=8) {
                                                        currentUser.updatePassword(newPass);
                                                        dialog.cancel();
                                                    }
                                                    else
                                                    {View view1 = findViewById(android.R.id.content);
                                                    Snackbar snackbar = Snackbar.make(view1,"Помилка - Ви ввели новий пароль, який має менше 8 символів!",Snackbar.LENGTH_LONG);
                                                    snackbar = ReadWrite.ChangeSnack(snackbar, getApplicationContext());
                                                    snackbar.show();}

                                                } else {
                                                    Log.e(TAG, "Error re-authenticating user.", task.getException());
                                                    View view1 = findViewById(android.R.id.content);
                                                    Snackbar snackbar = Snackbar.make(view1,"Помилка - Ви ввели невірний поточний пароль!",Snackbar.LENGTH_LONG);
                                                    snackbar = ReadWrite.ChangeSnack(snackbar, getApplicationContext());
                                                    snackbar.show();
                                                }
                                            });
                                }
                            });
                        }
                    });
                }
                photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, PICK_IMAGE_REQUEST);
                    }
                });
            }
        }

        if(userEmail!=null)
        {
            mail.setText(userEmail);
        }
        TextView exit = (TextView) findViewById(R.id.textLogout);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        query.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                TextView complete = findViewById(R.id.textView19);
                TextView not_complete = findViewById(R.id.textView20);
                count = 0;
                count_completed = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Retrieve task data from database
                    HashMap<String, Object> taskMap = (HashMap<String, Object>) snapshot.getValue();

                    Boolean completed = (Boolean) taskMap.get("completed");

                    if(completed==true)count_completed++;
                    if(completed==false) count++;
                }
                if(count_completed==0 || (count_completed>=5 && count_completed<=20)||count_completed%10==0)complete.setText(count_completed + " Завдань виконано");
                else complete.setText(count_completed + " Завдання виконано");
                if(count==0 || (count>=5 && count<=20)||count%10==0)not_complete.setText(count + " Завдань залишилось");
                else not_complete.setText(count + " Завдання залишилось");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "Error reading database", databaseError.toException());
            }
        });


        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("isAuthenticated", false);
                editor.apply();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this, WelcomeActivity.class);
                startActivity(intent);
            }
        });

        int selectedItemId = getIntent().getIntExtra("selectedItemId", R.id.goals);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(selectedItemId);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.home:
                        goToActivity(MainActivity.class, R.id.home);
                        return true;
                    case R.id.goals:
                        goToActivity(GoalsActivity.class, R.id.goals);
                        return true;
                    case R.id.calendar:
                        goToActivity(CalendarActivity.class, R.id.calendar);
                        return true;
                    case R.id.profile:return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView = findViewById(R.id.imageView5);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    void goToActivity(Class<?> activityClass, int selectedItemId) {
        Intent intent = new Intent(this, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("selectedItemId", selectedItemId);
        startActivity(intent);
    }

}
