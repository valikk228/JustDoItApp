package com.example.kurs0va;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

public class Goal extends TaskOrGoal{
    private int count;
    private int count_is = 0;
    private int left_days;

    public Goal(){}

    public Goal(String id, String name, int count, int count_is, String category, LocalDateTime date, String email, int left_days) {
        super(id, name, category, email, date);
        this.count = count;
        this.count_is = count_is;
        this.left_days = left_days;
    }

    public ArrayList<Goal> Sort(ArrayList<?> items){
        ArrayList<Goal> goals = (ArrayList<Goal>) items;
        if(goals.size()!=0)
        {
            for(int i=0; i<goals.size();i++)
            {
                for(int j=0;j<goals.size()-1;j++)
                {
                    if(goals.get(j).getCount()==goals.get(j).getCount_is())
                    {
                        Collections.swap(goals, j, j + 1);
                    }
                }
            }
        }
        return goals;
    }


    public ArrayList<?> Filter(ArrayList<?> items, String text)
    {
        ArrayList<Goal> filter_tasks = new ArrayList<>();
        ArrayList<Goal> goals = (ArrayList<Goal>) items;
        for (Goal item : goals) {
            System.out.println(item.getName() + " " + text);
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filter_tasks.add(item);
                System.out.println("додано " + item.getName() );
            }
        }

        if (filter_tasks.size() != 0) {
            filter_tasks = (ArrayList<Goal>) filter_tasks.get(0).Sort(filter_tasks);
            System.out.println("Розмір списку: " + filter_tasks.size() );
        }
        return filter_tasks;
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount_is() {
        return count_is;
    }

    public void setCount_is(int count_is) {
        this.count_is = count_is;
    }

    public int getLast_days() {
        return left_days;
    }

    public void setLast_days(int last_days) {
        this.left_days = last_days;
    }
}
