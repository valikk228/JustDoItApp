package com.example.kurs0va;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

public class Task extends TaskOrGoal
{
    private String description;
    private int priority;
    private boolean completed;

    Task(){};

    Task(String id, String name, String description, LocalDateTime datetime, int p, String c, String email, boolean completed)
    {
        super(id,name,c,email,datetime);
        this.description = description;
        this.priority=p;
        this.completed = completed;
    };

    @Override
    public ArrayList<?> Filter(ArrayList<?> items, String text)
    {
        ArrayList<Task> filter_tasks = new ArrayList<>();
        ArrayList<Task> tasks = (ArrayList<Task>) items;
        for (Task item : tasks) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filter_tasks.add(item);
            }
        }

        if (filter_tasks.size() != 0) {
            filter_tasks = (ArrayList<Task>) filter_tasks.get(0).Sort(filter_tasks);
        }
        return filter_tasks;
    }

    public ArrayList<?> Sort(ArrayList<?> items)
    {
        ArrayList<Task> tasks = (ArrayList<Task>) items;
        if (tasks.size() != 0) {
            for (int i = 0; i < tasks.size(); i++) {
                for (int j = 0; j < tasks.size() - 1; j++) {
                    if (tasks.get(j).getPriority() > tasks.get(j + 1).getPriority()) {
                        Collections.swap(tasks, j, j + 1);
                    }
                }
            }
        }
        return tasks;
    }


    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

}
