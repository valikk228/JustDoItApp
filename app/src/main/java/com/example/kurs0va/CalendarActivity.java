package com.example.kurs0va;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CalendarActivity extends AppCompatActivity
{
    LocalDateTime currentDate = LocalDateTime.now();
    ReadWrite readWrite = new ReadWrite();
    ArrayList<Task> tasks = new ArrayList<Task>();
    ArrayList<Task> completed_tasks = new ArrayList<Task>();
    Query query;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity);

        TextView date = findViewById(R.id.textView35);
        date.setText(ReadWrite.getDate(currentDate));

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
                    case R.id.calendar: return true;
                    case R.id.profile:
                        goToActivity(ProfileActivity.class, R.id.profile);
                        return true;
                }
                return false;
            }
        });

        RadioButton not_complete = findViewById(R.id.radio0);
        RadioButton complete = findViewById(R.id.radio1);
        databaseReference = FirebaseDatabase.getInstance().getReference("tasks");
        query = databaseReference.orderByChild("email").equalTo(readWrite.get_UserEmail());

        valueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    String CurrentDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-M-d"));
                    readWrite.readTaskData(dataSnapshot);
                    readWrite.setCalendarTasks(CurrentDate);

                    tasks = readWrite.get_Tasks();
                    if(not_complete.isChecked())
                    {
                        set_not_complete();
                    }
                    completed_tasks = readWrite.get_Completed_Tasks();
                    if(complete.isChecked())
                    {
                        set_complete();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "Error reading database", databaseError.toException());
            }
        };


        query.addValueEventListener(valueEventListener);


        SearchView searchView = findViewById(R.id.searchView);
        searchView.setQuery("",false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(complete.isChecked()) filter(newText,true);
                else  filter(newText,false);
                return false;
            }
        });

        Button back = findViewById(R.id.button_back);
        Button forward = findViewById(R.id.button_forward);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDate=currentDate.minusDays(1);
                date.setText(ReadWrite.getDate(currentDate));
                query.addValueEventListener(valueEventListener);
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDate=currentDate.plusDays(1);
                date.setText(ReadWrite.getDate(currentDate));
                query.addValueEventListener(valueEventListener);
            }
        });

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                query.addValueEventListener(valueEventListener);

            }
        });
        not_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                query.addValueEventListener(valueEventListener);
            }
        });


    }


    public void set_complete()
    {
        ListView taskList2 = (ListView) findViewById(R.id.listView_tasks);
        TextView no_tasks = findViewById(R.id.textView46);
        TextView no_tasks2 = findViewById(R.id.textView47);
        ImageView no_tasks_img = findViewById(R.id.imageView8);

        if(completed_tasks.size() != 0) {
            completed_tasks = (ArrayList<Task>) completed_tasks.get(0).Sort(completed_tasks);
            no_tasks.setText("");
            no_tasks2.setVisibility(View.GONE);
            no_tasks_img.setVisibility(View.GONE);
            TaskAdapter2 adapter2 = new TaskAdapter2(CalendarActivity.this, completed_tasks);
            taskList2.setAdapter(adapter2);
        }
        else{
            no_tasks.setText("Немає виконаних завдань на вибрану дату");
            no_tasks_img.setVisibility(View.VISIBLE);
            no_tasks2.setVisibility(View.GONE);
            TaskAdapter2 adapter2 = new TaskAdapter2(CalendarActivity.this, completed_tasks);
            taskList2.setAdapter(adapter2);
        }


        taskList2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                query.addValueEventListener(valueEventListener);
                Intent intent = new Intent(CalendarActivity.this, CompletedTaskDetailsActivity.class);
                System.out.println(completed_tasks.get(i).getName());
                System.out.println(completed_tasks.get(i).getDate());
                System.out.println(completed_tasks.get(i).getCategory());
                Task temp = completed_tasks.get(i);
                intent.putExtra("task", temp);
                startActivity(intent);
            }
        });
    }


    public void filter(String text, boolean complete) {
        ArrayList<Task> filter_tasks = new ArrayList<>();
        ListView taskList = (ListView) findViewById(R.id.listView_tasks);

        if(!complete && tasks.size() != 0) {
            filter_tasks = (ArrayList<Task>) tasks.get(0).Filter(tasks,text);
            TaskAdapter adapter = new TaskAdapter(CalendarActivity.this, filter_tasks);
            taskList.setAdapter(adapter);
        }
        else if(completed_tasks.size() != 0){
            filter_tasks = (ArrayList<Task>) completed_tasks.get(0).Filter(completed_tasks,text);
            TaskAdapter2 adapter = new TaskAdapter2(CalendarActivity.this, filter_tasks);
            taskList.setAdapter(adapter);
        }
    }


    public void set_not_complete()
    {
        // Set up ListView adapter with updated tasks data and notify adapter of data change
        ListView taskList1 = (ListView) findViewById(R.id.listView_tasks);
        TextView no_tasks = findViewById(R.id.textView46);
        TextView no_tasks2 = findViewById(R.id.textView47);
        ImageView no_tasks_img = findViewById(R.id.imageView8);

        if(tasks.size()!=0) {
            tasks = (ArrayList<Task>) tasks.get(0).Sort(tasks);
            no_tasks.setText("");
            no_tasks2.setVisibility(View.GONE);
            no_tasks_img.setVisibility(View.GONE);
            TaskAdapter adapter1 = new TaskAdapter(CalendarActivity.this, tasks);
            taskList1.setAdapter(adapter1);
        }
        else {
            no_tasks.setText("Немає завдань на вибрану дату");
            no_tasks2.setVisibility(View.VISIBLE);
            no_tasks_img.setVisibility(View.VISIBLE);
            TaskAdapter adapter1 = new TaskAdapter(CalendarActivity.this, tasks);
            taskList1.setAdapter(adapter1);
        }

        taskList1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(CalendarActivity.this, TaskDetailsActivity.class);
                intent.putExtra("task", tasks.get(i));
                startActivity(intent);
            }
        });
    }
    void goToActivity(Class<?> activityClass, int selectedItemId) {
        Intent intent = new Intent(this, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("selectedItemId", selectedItemId);
        startActivity(intent);
    }
}
