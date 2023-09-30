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
import android.widget.RadioButton;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.HashMap;

public class TaskDetailsActivity extends AppCompatActivity
{
    EditText text1, text2;
    Task task;
    Dialog dialog;
    TextView name;
    TextView description;
    LocalDateTime Date;
    TextView date;
    TextView categ;
    TextView prioritet;
    ImageButton calendar,category,priority,newItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_details_activity);

        name = findViewById(R.id.task_name2);
        description = findViewById(R.id.task_description2);
        date = findViewById(R.id.task_date2);
        categ = findViewById(R.id.task_category2);
        prioritet = findViewById(R.id.task_priority2);

        dialog=new Dialog(TaskDetailsActivity.this);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userEmail = currentUser.getEmail();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tasks");
        Query query = databaseReference.orderByChild("email").equalTo(userEmail);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Retrieve task data from database
                    HashMap<String, Object> taskMap = (HashMap<String, Object>) snapshot.getValue();

                    Boolean completed = Boolean.parseBoolean(taskMap.get("completed").toString());

                    if (snapshot.getKey().equals(task.getId())&& !completed ) {
                        DatabaseReference completedRef = databaseReference.child(snapshot.getKey()).child("completed");
                        completedRef.setValue(true);
                        finish();
                    }
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
        RadioButton complete = findViewById(R.id.radioButton2);
        Button delete = findViewById(R.id.button6);


        complete.setOnClickListener(new View.OnClickListener() {
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
    public void createDialog()
    {
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(363636));

        TextView title = dialog.findViewById(R.id.textView15);
        title.setText("Редагувати завдання");

        dialog.setCancelable(true);
        dialog.show();

        text1 = dialog.findViewById(R.id.name_goal);
        text2 = dialog.findViewById(R.id.description_task);

        text1.setText(task.getName());
        text2.setText(task.getDescription());
        
        calendar = (ImageButton) dialog.findViewById(R.id.imageButton1);
        if (calendar != null)
        {
            calendar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    task.setName(text1.getText().toString());
                    task.setDescription(text2.getText().toString());
                    dialog.setContentView(R.layout.pick_date);
                    dialog.setCancelable(true);

                    CalendarView datePicker = dialog.findViewById(R.id.date_picker);
                    Calendar calendarr = Calendar.getInstance();
                    int year = calendarr.get(Calendar.YEAR);
                    int month = calendarr.get(Calendar.MONTH);
                    int dayOfMonth = calendarr.get(Calendar.DAY_OF_MONTH);
                    datePicker.setMinDate(calendarr.getTimeInMillis());

                    datePicker.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                        @Override
                        public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                            Calendar selectedDate = Calendar.getInstance();
                            selectedDate.set(i, i1, i2);

                            Instant instant = selectedDate.toInstant();
                            Date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

                            System.out.println(Date.toString());
                        }
                    });

                    Button button1 = dialog.findViewById(R.id.button3);
                    Button button2 = dialog.findViewById(R.id.button5);
                    dialog.show();

                    button1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            dialog.setContentView(R.layout.pick_time);
                            dialog.setCancelable(true);

                            Button button3 = dialog.findViewById(R.id.button3);
                            Button button4 = dialog.findViewById(R.id.button5);
                            dialog.show();
                            button3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view)
                                {
                                    TimePicker timePicker=dialog.findViewById(R.id.timePicker);
                                    int hour = timePicker.getHour();
                                    int minute = timePicker.getMinute();

                                    System.out.println(hour+":"+minute);
                                    if(Date==null)Date=task.getDate();
                                    Date = Date.withHour(hour);
                                    Date = Date.withMinute(minute);

                                    task.setDate(Date);

                                    createDialog();
                                }
                            });

                            button4.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view)
                                {
                                    calendar.callOnClick();
                                }
                            });
                        }
                    });

                    button2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            createDialog();
                        }
                    });
                }
            });
        }

        category = (ImageButton) dialog.findViewById(R.id.imageButton2);
        if (category != null)
        {
            category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    task.setName(text1.getText().toString());
                    task.setDescription(text2.getText().toString());
                    dialog.setContentView(R.layout.custom_dialog_category);

                    TextView title = dialog.findViewById(R.id.textView15);
                    title.setText("Змінити категорію");

                    dialog.setCancelable(false);
                    dialog.show();

                    Button button = dialog.findViewById(R.id.button4);


                    ImageButton education,music,work,health,sport,home,design,shopping,social,another;
                    education=dialog.findViewById(R.id.imageButton);
                    education.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            task.setCategory("Навчання");
                            createDialog();
                        }
                    });
                    music=dialog.findViewById(R.id.imageButton5);
                    music.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            task.setCategory("Музика");
                            createDialog();
                        }
                    });
                    work=dialog.findViewById(R.id.imageButton6);
                    work.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            task.setCategory("Робота");
                            createDialog();
                        }
                    });
                    health=dialog.findViewById(R.id.imageButton7);
                    health.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            task.setCategory("Здоров'я");
                            createDialog();
                        }
                    });
                    sport=dialog.findViewById(R.id.imageButton8);
                    sport.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            task.setCategory("Спорт");
                            createDialog();
                        }
                    });
                    home=dialog.findViewById(R.id.imageButton9);
                    home.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            task.setCategory("Дім");
                            createDialog();
                        }
                    });
                    design=dialog.findViewById(R.id.imageButton11);
                    design.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            task.setCategory("Дизайн");
                            createDialog();
                        }
                    });
                    shopping=dialog.findViewById(R.id.imageButton10);
                    shopping.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            task.setCategory("Покупки");
                            createDialog();
                        }
                    });
                    social=dialog.findViewById(R.id.imageButton12);
                    social.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            task.setCategory("Дозвілля");
                            createDialog();
                        }
                    });
                    another=dialog.findViewById(R.id.imageButton13);
                    another.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            task.setCategory("Інше");
                            createDialog();
                        }
                    });

                    button.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            createDialog();
                        }
                    });
                }
            });
        }


        priority = (ImageButton) dialog.findViewById(R.id.imageButton3);
        if (priority != null)
        {
            priority.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    task.setName(text1.getText().toString());
                    task.setDescription(text2.getText().toString());
                    dialog.setContentView(R.layout.pick_priority);

                    TextView title = dialog.findViewById(R.id.textView7);
                    title.setText("Змінити пріоритет");

                    dialog.setCancelable(true);
                    dialog.show();

                    Button button5 = dialog.findViewById(R.id.button5);
                    Button button6 = dialog.findViewById(R.id.button3);
                    Spinner spinner = dialog.findViewById(R.id.spinner);


                    button5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            createDialog();
                        }
                    });

                    button6.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            int index = spinner.getSelectedItemPosition();
                            System.out.println(index);
                            task.setPriority(index+1);
                            System.out.println(prioritet);
                            createDialog();
                        }
                    });
                }
            });
        }

        newItem = (ImageButton) dialog.findViewById(R.id.imageButton4);
        if (newItem != null)
        {
            newItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    if(task.getCategory() != "" && task.getPriority() != 0 && task.getDate() != null
                            && text1.getText().toString() != "" && text2.getText().toString() != "")
                    {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference ref = database.getReference("tasks");
                        task.setName(text1.getText().toString());
                        task.setDescription(text2.getText().toString());
                        ref.child(task.getId()).setValue(task);
                    }
                    else
                    {
                    }
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
                    dialog.cancel();
                }
            });
        }
    }
}

