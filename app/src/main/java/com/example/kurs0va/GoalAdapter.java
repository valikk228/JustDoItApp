package com.example.kurs0va;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

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
import java.util.HashMap;
import java.util.List;

public class GoalAdapter extends ArrayAdapter<Goal> {

    private final ArrayList<Goal> goals;
    Dialog dialog ;
    FirebaseUser currentUser;
    View listItemView;
    FirebaseAuth auth;
    ImageButton Add;
    Context cont;
    EditText text1, text2, text3;
    String t1 = "", t2 = "", t3="";
    int i;
    LocalDateTime date;

    public GoalAdapter(Context context, ArrayList<Goal> goals) {
        super(context, R.layout.goal_item, goals);
        this.goals = goals;
        this.cont = context;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        listItemView = convertView;



        if (listItemView == null)
        {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.goal_item, parent, false);
        }
        dialog = new Dialog(listItemView.getContext());

        Goal dataItem = goals.get(position);

        Add = listItemView.findViewById(R.id.imageButton16);

        TextView name = listItemView.findViewById(R.id.task_name);
        name.setText(dataItem.getName());

        TextView category = listItemView.findViewById(R.id.task_category);
        category.setText(dataItem.getCategory());

        TextView date = listItemView.findViewById(R.id.task_date);

        LocalDateTime temp1 = dataItem.getDate();
        date.setText(ReadWrite.getDate(temp1));

        TextView count = listItemView.findViewById(R.id.textView44);
        count.setText(dataItem.getCount_is() + "/" + dataItem.getCount());

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        TextView left_day = listItemView.findViewById(R.id.textView45);
        int days = (int) (ChronoUnit.DAYS.between(temp1,LocalDateTime.now())-1)*-1;
        if(LocalDate.from(temp1).compareTo(LocalDate.now())<=0)days=0;
        int left_days1 = dataItem.getLast_days()+1;
        if(days!=0) {
            left_day.setText("Залишилось " + String.valueOf(days) + " днів із " + left_days1);
        }
        else {left_day.setText("Вийшов термін виконання завдання");}
        ImageButton edit = listItemView.findViewById(R.id.imageButton17);

        ProgressBar complete = listItemView.findViewById(R.id.progressBar);
        complete.setMax(dataItem.getCount());
        complete.setProgress(dataItem.getCount_is());
        ProgressBar percentDays = listItemView.findViewById(R.id.progressBar2);
        percentDays.setMax(left_days1);
        percentDays.setProgress(left_days1-(left_days1 - (left_days1-days)));

        TextView progress1 =listItemView.findViewById(R.id.textView42);
        TextView progress2 =listItemView.findViewById(R.id.textView43);

        int percentDays2 = (int) Math.round((left_days1*1.0-days*1.0)/left_days1*1.0*100.0);
        progress2.setText(percentDays2 + " %");


        int percent = (int) Math.round(dataItem.getCount_is()*1.0 / dataItem.getCount()*1.0 * 100);
        if((dataItem.getCount_is()/dataItem.getCount())<1) {
            progress1.setText(percent + "%");
        }
        else progress1.setText("100%");
        if(progress1.getText().equals("100%"))
        {
            Add.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
            name.setTextColor(Color.GREEN);
            left_day.setText("Вітаємо! Ви досягли поставленої цілі!");
        }
        else
        {
            edit.setVisibility(View.VISIBLE);
            Add.setVisibility(View.VISIBLE);
            name.setTextColor(Color.WHITE);
        }


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = position;
            createDialog();
            }
        });

        ImageButton Delete = listItemView.findViewById(R.id.imageButton15);
        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String email = user.getEmail();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("goals");
                Query query = userRef.orderByChild("email").equalTo(email);

                query.addValueEventListener( new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if(snapshot.getKey().equals(goals.get(position).getId()))
                            {
                               userRef.child(goals.get(position).getId()).removeValue();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("TAG", "Error reading database", databaseError.toException());
                    }
                });
            }
        });

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.plus_count);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(363636));
                dialog.setCancelable(true);
                dialog.show();

                EditText plus = dialog.findViewById(R.id.editTextNumber);
                Button ok = dialog.findViewById(R.id.button8);

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!plus.getText().toString().equals(""))
                        {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String email = user.getEmail();
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("goals");

                            Query query = userRef.orderByChild("email").equalTo(email);

                            query.addValueEventListener( new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        if(snapshot.getKey().equals(goals.get(position).getId()))
                                        {
                                            goals.get(position)
                                                    .setCount_is(goals.get(position).
                                                            getCount_is()+Integer.parseInt(plus.getText().toString()));
                                            userRef.child(goals.get(position).getId()).setValue(goals.get(position));
                                            dialog.cancel();
                                        }
                                    }
                                    query.removeEventListener(this);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e("TAG", "Error reading database", databaseError.toException());
                                }
                            });
                        }
                    }
                });
            }
        });
        return listItemView;
    }

    public void createDialog()
    {
        dialog.setContentView(R.layout.dialog_goal);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(363636));
        dialog.setCancelable(true);
        dialog.show();

        boolean t=true;

        text1 = dialog.findViewById(R.id.name_goal);
        text2 = dialog.findViewById(R.id.count_goal);
        text3 = dialog.findViewById(R.id.name_category);

        if(t)t1=goals.get(i).getName();
        if(t)t2= String.valueOf(goals.get(i).getCount());
        if(t)t3=goals.get(i).getCategory();

        t = false;
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
                        goal.setCount_is(goals.get(i).getCount_is());
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

                        ref.child(goals.get(i).getId()).setValue(goal);
                        dialog.cancel();
                    }
                    else
                    {
                    }

                }
            });
        }
    }
}


