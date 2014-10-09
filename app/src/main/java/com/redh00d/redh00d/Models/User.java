package com.redh00d.redh00d.Models;

/**
 * Created by Maxime on 10/9/2014.
 */
public class User {

    private Long ID;
    private String email;
    private String password;

    public User(Long ID, String email, String password) {
        this.ID = ID;
        this.email = email;
        this.password = password;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
