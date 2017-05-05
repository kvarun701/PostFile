package com.abeer.postfile;

/**
 * Created by Pawan on 05-01-2017.
 */

public class SpinnerModelView {
    private int Id;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public SpinnerModelView(int id, String name) {
        this.Id = id;
        this.name = name;
    }
}
