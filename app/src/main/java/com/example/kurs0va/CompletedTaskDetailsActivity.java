package com.example.kurs0va;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.HashMap;

public class CompletedTaskDetailsActivity extends TaskDetailsActivity
{
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.completed_task_details_activity);

        name = findViewById(R.id.task_name2);
        description = findViewById(R.id.task_description2);
        date = findViewById(R.id.task_date2);
        categ = findViewById(R.id.task_category2);
        prioritet = findViewById(R.id.task_priority2);

        dialog=new Dialog(CompletedTaskDetailsActivity.this);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userEmail = currentUser.getEmail();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tasks");
        Query query = databaseReference.orderByChild("email").equalTo(userEmail);

        // создать слушателя базы данных
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Retrieve task data from database
                    HashMap<String, Object> taskMap = (HashMap<String, Object>) snapshot.getValue();

                    Boolean completed = Boolean.parseBoolean(taskMap.get("completed").toString());

                    if (snapshot.getKey().equals(task.getId())&& completed )
                    {
                        count++;
                        DatabaseReference completedRef = databaseReference.child(snapshot.getKey()).child("completed");
                        completedRef.setValue(false);
                        break;
                    }
                }
                if(count!=0){
                    finish();
                }
                query.removeEventListener(this);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "Error reading database", databaseError.toException());
            }
        };

        Intent intent = getIntent();
        task = (Task) intent.getSerializableExtra("task");

        name.setText(task.getName());
        description.setText(task.getDescription());
        date.setText(ReadWrite.getDate(task.getDate()));
        categ.setText(task.getCategory());

        switch(task.getPriority())
        {
            case 1:prioritet.setText("Обов'язковий");break;
            case 2:prioritet.setText("Високий");break;
            case 3:prioritet.setText("Середній");break;
            case 4:prioritet.setText("Низький");break;
            case 5:prioritet.setText("Дуже низький");break;
        }

        ImageButton exit = findViewById(R.id.imageButton14);
        ImageButton edit = findViewById(R.id.imageButton16);
        Button return_button = findViewById(R.id.button_return);
        Button delete = findViewById(R.id.button6);

        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                query.addValueEventListener(valueEventListener);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            createDialog();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            snapshot.getRef().child(task.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task)
                                {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Data is removed successfully!");
                                        finish();
                                    } else {
                                        Log.e(TAG, "Failed to remove data.", task.getException());
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                        });
                    }
        });
    }


}

