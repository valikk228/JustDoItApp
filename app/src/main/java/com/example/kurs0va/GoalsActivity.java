package com.example.kurs0va;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class GoalsActivity extends AppCompatActivity
{
    ArrayList<Goal> goals = new ArrayList<Goal>();
    ReadWrite readWrite = new ReadWrite();
    Dialog dialog;
    EditText text1, text2, text3;
    String t1 = "", t2 = "", t3="";
    LocalDateTime date;
    FirebaseUser currentUser;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goals_activity);

        int selectedItemId = getIntent().getIntExtra("selectedItemId", R.id.goals);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(selectedItemId);

        dialog=new Dialog(GoalsActivity.this);
        findViewById(R.id.fab2).setOnClickListener(view -> createDialog());

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        String userEmail = currentUser.getEmail();
        ListView taskList1 = (ListView) findViewById(R.id.goal_listView);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("goals");
        Query query = databaseReference.orderByChild("email").equalTo(userEmail);


        query.addValueEventListener( new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView goals_ = findViewById(R.id.textView48);
                TextView goals_2 = findViewById(R.id.textView49);
                ImageView goals_img = findViewById(R.id.imageView9);

                readWrite.readGoalData(dataSnapshot);
                goals = readWrite.getGoals();
                if(goals.size()!=0) {
                    goals = goals.get(0).Sort(goals);
                    goals_.setText("");
                    goals_2.setText("");
                    goals_img.setVisibility(View.GONE);
                }
                else{
                    goals_.setText("Хочете досягти поставлених цілей?");
                    goals_2.setText("Натисніть +. щоб добавити ціль");
                    goals_img.setVisibility(View.VISIBLE);
                }
                GoalAdapter adapter1 = new GoalAdapter(GoalsActivity.this, goals);
                taskList1.setAdapter(adapter1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "Error reading database", databaseError.toException());
            }
        });

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.home:
                        goToActivity(MainActivity.class, R.id.home);
                        return true;
                    case R.id.goals:return true;
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
    }

    public void createDialog()
    {
        dialog.setContentView(R.layout.dialog_goal);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(363636));
        dialog.setCancelable(true);
        dialog.show();

        text1 = dialog.findViewById(R.id.name_goal);
        text2 = dialog.findViewById(R.id.count_goal);
        text3 = dialog.findViewById(R.id.name_category);

        text1.setText(t1);
        text2.setText(t2);
        text3.setText(t3);


        Button calendar = dialog.findViewById(R.id.button7);
        if(date!=null)calendar.setText(date.format(DateTimeFormatter.ofPattern("d-M-yyyy")));
        else{
            date = LocalDateTime.now();
            calendar.setText(date.format(DateTimeFormatter.ofPattern("d-M-yyyy")));
        }

        if (calendar != null)
        {
            calendar.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    t1 = text1.getText().toString();
                    t2 = text2.getText().toString();
                    t3 = text3.getText().toString();

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
                            date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

                            System.out.println(date.toString());
                        }
                    });

                    Button button1 = dialog.findViewById(R.id.button3);
                    button1.setText("Продовжити");
                    Button button2 = dialog.findViewById(R.id.button5);

                    button2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            createDialog();
                        }
                    });

                    button1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            createDialog();
                        }
                    });
                    dialog.show();
                }
            });
        }

        ImageButton newGoal = (ImageButton) dialog.findViewById(R.id.imageButton4);
        if (newGoal != null)
        {
            newGoal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    Goal goal = new Goal();

                    if(!text3.getText().toString().equals("") && !text2.getText().toString().equals("")
                            && date != null
                    )
                    {
                        goal.setEmail(currentUser.getEmail());
                        goal.setName(text1.getText().toString());
                        goal.setCount(Integer.parseInt(String.valueOf(text2.getText())));
                        goal.setCategory(text3.getText().toString());
                        goal.setDate(date);

                        System.out.println(text3.getText().toString() + " " + text2.getText().toString());
                        System.out.println(date.toString());
                        System.out.println(t1 + " " + t2);
                        System.out.println(text1.getText().toString());

                        goal.setLast_days((int) (ChronoUnit.DAYS.between(date,LocalDateTime.now())*-1));

                        t1="";
                        t2="";
                        t3="";
                        date=null;
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference ref = database.getReference("goals");
                        String goalId = ref.push().getKey();
                        ref.child(goalId).setValue(goal);
                        dialog.cancel();
                    }
                    else
                    {
                        View view1 = findViewById(android.R.id.content);
                        Snackbar snackbar= Snackbar.make(view1, "Ви не заповнили усі поля!", Snackbar.LENGTH_LONG);
                        snackbar=ReadWrite.ChangeSnack(snackbar,getApplicationContext());
                        snackbar.show();
                    }

                }
            });
        }
    }

    void goToActivity(Class<?> activityClass, int selectedItemId) {
        Intent intent = new Intent(this, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("selectedItemId", selectedItemId);
        startActivity(intent);
    }


    public void filter(String text) {
        ArrayList<Goal> filter_goals = new ArrayList<>();
        ListView taskList = (ListView) findViewById(R.id.goal_listView);

        if(goals.size()!=0) {
        filter_goals = (ArrayList<Goal>) goals.get(0).Filter(goals,text);
        }
        GoalAdapter adapter = new GoalAdapter(GoalsActivity.this, filter_goals);
        taskList.setAdapter(adapter);
        }
}

