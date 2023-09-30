package com.example.kurs0va;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;


public class ReadWrite {
    private FirebaseUser currentUser;
    ArrayList<Task> alltasks = new ArrayList<Task>();
    private ArrayList<Task> tasks;
    private ArrayList<Task> completed_tasks;
    private ArrayList<Goal> goals;
    private FirebaseAuth auth;
    private final String userEmail;
    private  DateTimeFormatter formatter;
    private DateTimeFormatter formatter2;

    ReadWrite(){
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        userEmail = currentUser.getEmail();
        formatter = DateTimeFormatter.ofPattern("yyyy-M-d'T'H:m");
        formatter2 = DateTimeFormatter.ofPattern("yyyy-M-d");
    }
    public String get_UserEmail()
    {
        return this.userEmail;
    }
    public ArrayList<Task> get_Tasks()
    {
        return this.tasks;
    }
    public ArrayList<Task> get_Completed_Tasks()
    {
        return this.completed_tasks;
    }

    public ArrayList<Goal> getGoals()
    {
        return goals;
    }

    public void readTaskData(DataSnapshot dataSnapshot)
    {
        alltasks = new ArrayList<Task>();

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            // Retrieve task data from database
            HashMap<String, Object> taskMap = (HashMap<String, Object>) snapshot.getValue();
            String name = (String) taskMap.get("name");
            String description = (String) taskMap.get("description");
            String category = (String) taskMap.get("category");
            Boolean completed = Boolean.parseBoolean(taskMap.get("completed").toString());

            HashMap<String, Object> datetimeMap = (HashMap<String, Object>) taskMap.get("date");
            System.out.println(datetimeMap);
            String dayOfMonth = datetimeMap.get("dayOfMonth").toString();
            String monthValue = datetimeMap.get("monthValue").toString();
            String year = datetimeMap.get("year").toString();
            String dateString = year + "-" + monthValue + "-" + dayOfMonth;
            String hour = datetimeMap.get("hour").toString();
            String minute = datetimeMap.get("minute").toString();
            String timeString = hour + ":" + minute;
            String datetimeString = dateString + "T" + timeString;
            System.out.println(dateString);

            LocalDateTime datetime = LocalDateTime.now();
            if (dateString != null && timeString != null) {
                datetime = LocalDateTime.parse(datetimeString, formatter);
            }

            int priority = 0;
            Object priorityObj = taskMap.get("priority");
            if (priorityObj instanceof Long) {
                priority = ((Long) priorityObj).intValue();
            }

            // Create new Task object and add it to tasks ArrayList
            Task task_ = new Task(snapshot.getKey(), name, description, datetime, priority, category, userEmail, completed);

            alltasks.add(task_);

        }
    }



    public void readGoalData(DataSnapshot dataSnapshot)
    {
        goals = new ArrayList<Goal>();

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            // Retrieve task data from database
            HashMap<String, Object> taskMap = (HashMap<String, Object>) snapshot.getValue();
            String name = (String) taskMap.get("name");
            int count1 = Integer.parseInt(taskMap.get("count").toString());
            int count2 = Integer.parseInt(taskMap.get("count_is").toString());
            int last_days1 = Integer.parseInt(taskMap.get("last_days").toString());
            String category = (String) taskMap.get("category");

            HashMap<String, Object> datetimeMap = (HashMap<String, Object>) taskMap.get("date");
            System.out.println(datetimeMap);
            String dayOfMonth = datetimeMap.get("dayOfMonth").toString();
            String monthValue = datetimeMap.get("monthValue").toString();
            String year = datetimeMap.get("year").toString();
            String dateString = year + "-" + monthValue + "-" + dayOfMonth;
            String hour = String.valueOf(LocalDateTime.now().getHour());
            String minute = String.format("%02d", LocalDateTime.now().getMinute());
            String timeString = hour + ":" + minute;
            String datetimeString = dateString + "T" + timeString;
            System.out.println(dateString);

            LocalDateTime datetime = LocalDateTime.now();
            if (dateString != null && timeString != null) {
                datetime = LocalDateTime.parse(datetimeString, DateTimeFormatter.ofPattern("yyyy-M-d'T'H:m"));
            }

            Goal goal_ = new Goal(snapshot.getKey(), name,count1,count2, category,datetime,userEmail, last_days1);
            goals.add(goal_);
        }
    }


    public void setTasks()
    {
        tasks = new ArrayList<Task>();
        completed_tasks = new ArrayList<Task>();

        String datetimeNow = LocalDateTime.now().format(formatter2);

        for(int i=0;i<alltasks.size();i++) {
            String datetime2 = alltasks.get(i).getDate().format(formatter2);

            if (datetimeNow.equals(datetime2) && !alltasks.get(i).getCompleted()) {
                tasks.add(alltasks.get(i));
            }
            if (datetimeNow.equals(datetime2) && alltasks.get(i).getCompleted()) {
                completed_tasks.add(alltasks.get(i));
            }

        }
    }

    public void setCalendarTasks(String CurrentDate)
    {
        tasks = new ArrayList<Task>();
        completed_tasks = new ArrayList<Task>();

        for(int i=0;i<alltasks.size();i++) {
            String datetime2 = alltasks.get(i).getDate().format(formatter2);

            if (CurrentDate.equals(datetime2) && !alltasks.get(i).getCompleted()) {
                tasks.add(alltasks.get(i));
            }
            if (CurrentDate.equals(datetime2) && alltasks.get(i).getCompleted()) {
                completed_tasks.add(alltasks.get(i));
            }
        }
    }


    public static String getDate(LocalDateTime date)
    {
        String text = "";
        switch(date.getMonthValue())
        {
            case 1: text = date.getDayOfMonth() + " Січня " + date.getYear();break;
            case 2: text = date.getDayOfMonth() + " Лютого " + date.getYear();break;
            case 3: text = date.getDayOfMonth() + " Березня " + date.getYear();break;
            case 4: text = date.getDayOfMonth() + " Квітня " + date.getYear();break;
            case 5: text = date.getDayOfMonth() + " Травня " + date.getYear();break;
            case 6: text = date.getDayOfMonth() + " Червня " + date.getYear();break;
            case 7: text = date.getDayOfMonth() + " Липня " + date.getYear();break;
            case 8: text = date.getDayOfMonth() + " Серпня " + date.getYear();break;
            case 9: text = date.getDayOfMonth() + " Вересня " + date.getYear();break;
            case 10:text = date.getDayOfMonth() + " Жовтня " + date.getYear();break;
            case 11:text = date.getDayOfMonth() + " Листопада " + date.getYear();break;
            case 12:text = date.getDayOfMonth() + " Грудня " + date.getYear();break;
        }
        return text;
    }


    public static Snackbar ChangeSnack(Snackbar snackbar, Context context)
    {
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.purple));
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        return snackbar;
    }


}
