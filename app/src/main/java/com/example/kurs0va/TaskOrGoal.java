package com.example.kurs0va;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class TaskOrGoal implements Serializable {
    private String id;
    private String name,category,email;
    private LocalDateTime date;

    TaskOrGoal(){}
    public TaskOrGoal(String id, String name, String category, String email, LocalDateTime date) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.email = email;
        this.date = date;
    }

    public abstract ArrayList<?> Filter(ArrayList<?> items, String text);

    public abstract ArrayList<?> Sort(ArrayList<?> items);



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
