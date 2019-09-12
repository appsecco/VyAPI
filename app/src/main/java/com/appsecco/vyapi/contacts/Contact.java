package com.appsecco.vyapi.contacts;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "contacts_table")
public class Contact {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String fname;
    private String lname;
    private String phonenumber;
    private String email;
    private String website;
    private String location;

    @Ignore
    public Contact() {
    }

    public Contact(String fname, String lname, String phonenumber, String email, String website, String location) {
        this.fname = fname;
        this.lname = lname;
        this.phonenumber = phonenumber;
        this.email = email;
        this.website = website;
        this.location = location;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public String getEmail() {
        return email;
    }

    public String getWebsite() {
        return website;
    }

    public String getLocation() {
        return location;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
