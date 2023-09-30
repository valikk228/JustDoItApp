package com.example.kurs0va;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements Serializable
{
    ArrayList<Task> tasks = new ArrayList<Task>();
    ReadWrite readWrite = new ReadWrite();
    ArrayList<Task> completed_tasks = new ArrayList<Task>();
    EditText text1, text2;
    DatabaseReference databaseReference;
    Query query;
    String t1 = "", t2 = "";
    LocalDateTime dateTime;
    int prioritet = 0;
    Dialog dialog;
    String categ = "";
    ImageButton calendar,category,priority,newItem;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        ImageView imageView = findViewById(R.id.imageView3);
        TextView textView1 = findViewById(R.id.textView13);
        TextView textView2 = findViewById(R.id.textView14);
        TextView today = findViewById(R.id.textView18);
        TextView compl = findViewById(R.id.textView17);
        databaseReference = FirebaseDatabase.getInstance().getReference("tasks");
        query = databaseReference.orderByChild("email").equalTo(readWrite.get_UserEmail());


        query.addValueEventListener( new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                readWrite.readTaskData(dataSnapshot);
                readWrite.setTasks();

                // Заповнюємо ListViews
                ListView taskList1 = (ListView) findViewById(R.id.listView);
                tasks = readWrite.get_Tasks();
                if(tasks.size()!=0) {
                    tasks = (ArrayList<Task>) tasks.get(0).Sort(tasks);
                }
                TaskAdapter adapter1 = new TaskAdapter(MainActivity.this, tasks);
                taskList1.setAdapter(adapter1);

                ListView taskList2 = (ListView) findViewById(R.id.listView2);
                completed_tasks = readWrite.get_Completed_Tasks();
                if(completed_tasks.size()!=0) {
                    completed_tasks = (ArrayList<Task>) completed_tasks.get(0).Sort(completed_tasks);
                }
                TaskAdapter2 adapter2 = new TaskAdapter2(MainActivity.this, completed_tasks);
                taskList2.setAdapter(adapter2);

                taskList1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(MainActivity.this, TaskDetailsActivity.class);
                        intent.putExtra("task", tasks.get(i));
                        startActivity(intent);
                    }
                });

                taskList2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(MainActivity.this, CompletedTaskDetailsActivity.class);
                        intent.putExtra("task", completed_tasks.get(i));
                        startActivity(intent);
                    }
                });

                if (tasks.size() == 0 && completed_tasks.size()==0) {
                    imageView.setVisibility(View.VISIBLE);
                    textView1.setVisibility(View.VISIBLE);
                    textView2.setVisibility(View.VISIBLE);
                    today.setVisibility(View.GONE);
                    compl.setVisibility(View.GONE);
                } else {
                    imageView.setVisibility(View.GONE);
                    textView1.setVisibility(View.GONE);
                    textView2.setVisibility(View.GONE);
                    today.setVisibility(View.VISIBLE);
                    compl.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "Error reading database", databaseError.toException());
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.home: return true;
                    case R.id.goals:
                        goToActivity(GoalsActivity.class, R.id.goals);
                        return true;
                    case R.id.calendar:
                        goToActivity(CalendarActivity.class, R.id.calendar);
                        return true;
                    case R.id.profile:
                        goToActivity(ProfileActivity.class, R.id.profile);
                        return true;
                }
                return false;
            }
        });
        dialog=new Dialog(MainActivity.this);
        findViewById(R.id.fab).setOnClickListener(view -> createDialog());
    }



    void goToActivity(Class<?> activityClass, int selectedItemId) {
        Intent intent = new Intent(this, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("selectedItemId", selectedItemId);
        startActivity(intent);
    }

    public void createDialog()
    {
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(363636));
        dialog.setCancelable(true);
        dialog.show();

        text1 = dialog.findViewById(R.id.name_goal);
        text2 = dialog.findViewById(R.id.description_task);

        text1.setText(t1);
        text2.setText(t2);


        calendar = (ImageButton) dialog.findViewById(R.id.imageButton1);
        if (calendar != null)
        {
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                t1 = text1.getText().toString();
                t2 = text2.getText().toString();
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
                        dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

                        System.out.println(dateTime.toString());
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
                        TimePicker timePicker=dialog.findViewById(R.id.timePicker);
                        dialog.show();
                        button3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                TimePicker timePicker=dialog.findViewById(R.id.timePicker);
                                int hour = timePicker.getHour();
                                int minute = timePicker.getMinute();

                                System.out.println(hour+":"+minute);
                                if(dateTime==null)dateTime=LocalDateTime.now();
                                dateTime = dateTime.withHour(hour);
                                dateTime = dateTime.withMinute(minute);

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
                    t1 = text1.getText().toString();
                    t2 = text2.getText().toString();
                    dialog.setContentView(R.layout.custom_dialog_category);
                    dialog.setCancelable(false);
                    dialog.show();

                    Button button = dialog.findViewById(R.id.button4);


                    ImageButton education,music,work,health,sport,home,design,shopping,social,another;
                    education=dialog.findViewById(R.id.imageButton);
                    education.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                         categ="Навчання";
                         createDialog();
                        }
                    });
                    music=dialog.findViewById(R.id.imageButton5);
                    music.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            categ="Музика";
                            createDialog();
                        }
                    });
                    work=dialog.findViewById(R.id.imageButton6);
                    work.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            categ="Робота";
                            createDialog();
                        }
                    });
                    health=dialog.findViewById(R.id.imageButton7);
                    health.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            categ="Здоров'я";
                            createDialog();
                        }
                    });
                    sport=dialog.findViewById(R.id.imageButton8);
                    sport.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            categ="Спорт";
                            createDialog();
                        }
                    });
                    home=dialog.findViewById(R.id.imageButton9);
                    home.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            categ="Дім";
                            createDialog();
                        }
                    });
                    design=dialog.findViewById(R.id.imageButton11);
                    design.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            categ="Дизайн";
                            createDialog();
                        }
                    });
                    shopping=dialog.findViewById(R.id.imageButton10);
                    shopping.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            categ="Покупки";
                            createDialog();
                        }
                    });
                    social=dialog.findViewById(R.id.imageButton12);
                    social.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            categ="Дозвілля";
                            createDialog();
                        }
                    });
                    another=dialog.findViewById(R.id.imageButton13);
                    another.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            categ="Інше";
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
                    t1 = text1.getText().toString();
                    t2 = text2.getText().toString();
                    dialog.setContentView(R.layout.pick_priority);
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
                         prioritet=index+1;
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
                    Task task = new Task();
                    if(!categ.equals("") && prioritet != 0 && dateTime != null
                            && !t1.equals("")
                            && !t2.equals("")
                            && text1 != null
                            && text2 != null
                    )
                    {
                        task.setEmail(readWrite.get_UserEmail());
                        task.setName(text1.getText().toString());
                        task.setDescription(text2.getText().toString());
                        task.setCategory(categ);
                        task.setPriority(prioritet);
                        task.setDate(dateTime);
                        task.setCompleted(false);

                        t1="";
                        t2="";
                        categ="";
                        prioritet=0;
                        dateTime=null;
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference ref = database.getReference("tasks");
                        String taskId = ref.push().getKey();
                        ref.child(taskId).setValue(task);
                        dialog.cancel();
                    }
                    else
                    {
                        View view1 = findViewById(android.R.id.content);
                        Snackbar snackbar= Snackbar.make(view1, "Ви не заповнили усі поля!", Snackbar.LENGTH_LONG);
                        snackbar=ReadWrite.ChangeSnack(snackbar, getApplicationContext());
                        snackbar.show();
                    }
                    System.out.println(text1.getText().toString() + " " + text2.getText().toString());
                    System.out.println(prioritet + " " + categ + " " + dateTime);
                    System.out.println(task.getName() + " " + task.getDescription() +
                            " " + task.getCategory() + " " + task.getDate() + " " + task.getPriority());
                }
            });
        }
    }
}