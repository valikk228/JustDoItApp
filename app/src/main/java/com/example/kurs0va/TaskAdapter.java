package com.example.kurs0va;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {

    private final List<Task> tasks;
    int selectedPosition = 0, count=0;

    public TaskAdapter(Context context, List<Task> tasks) {
        super(context, R.layout.task_item, tasks);
        this.tasks = tasks;
    }
    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null)
        {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.task_item, parent, false);
        }

        Task dataItem = tasks.get(position);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userEmail = currentUser.getEmail();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tasks");
        Query query = databaseReference.orderByChild("email").equalTo(userEmail);

        // создать слушателя базы данных
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                selectedPosition=position;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Retrieve task data from database
                    HashMap<String, Object> taskMap = (HashMap<String, Object>) snapshot.getValue();

                    Boolean completed = Boolean.parseBoolean(taskMap.get("completed").toString());

                    if (snapshot.getKey().equals(tasks.get(selectedPosition).getId())&& !completed ) {
                        count++;
                        DatabaseReference completedRef = databaseReference.child(snapshot.getKey()).child("completed");
                        completedRef.setValue(true);
                        break;
                    }
                }
                query.removeEventListener(this);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "Error reading database", databaseError.toException());
            }
        };
        TextView name = listItemView.findViewById(R.id.task_name);
        name.setText(dataItem.getName());

        TextView category = listItemView.findViewById(R.id.task_category);
        category.setText(dataItem.getCategory());

        TextView priority = listItemView.findViewById(R.id.task_priority);
        priority.setText(Integer.toString(dataItem.getPriority()));

        TextView date = listItemView.findViewById(R.id.task_date);
        TextView time = listItemView.findViewById(R.id.task_time);

        final DateTimeFormatter CUSTOM_FORMATTER1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final DateTimeFormatter CUSTOM_FORMATTER2 = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime temp1 = dataItem.getDate();

        String formattedString2 = temp1.format(CUSTOM_FORMATTER2);
        date.setText(ReadWrite.getDate(temp1));
        time.setText(formattedString2);

        RadioButton r = (RadioButton)listItemView.findViewById(R.id.radioButton);

        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                query.addValueEventListener(valueEventListener);
            }
        });

        return listItemView;
    }

}