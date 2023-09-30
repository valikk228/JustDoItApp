package com.example.kurs0va;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TaskAdapter2 extends ArrayAdapter<Task> {

    private final List<Task> tasks;

    public TaskAdapter2(Context context, List<Task> tasks) {
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

        TextView name = listItemView.findViewById(R.id.task_name);
        name.setTextColor(Color.parseColor("#FF0000"));
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
        r.setVisibility(View.GONE);
        return listItemView;
    }

}