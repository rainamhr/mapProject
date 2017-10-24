package com.example.queen.parproject.model;

/**
 * Created by queen on 10/4/17.
 */

public class User {

    String uid;
    String name;
    String address;
    String number;
    String email;
    String password;/*
    String gps;
    double latitude;
    double longitude;*/
    private String key;

    public User(){

    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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

  /*  public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
*/
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public User(String uid, String name, String address, String number, String email, String password,
                String gps, double latitude, double longitude) {
        this.uid = uid;
        this.name = name;
        this.address = address;
        this.number = number;
        this.email = email;
        this.password = password;/*
        this.gps = gps;
        this.latitude = latitude;
        this.longitude = longitude;*/
    }

    public User(User user,String key) {
        this.uid = user.getUid();
        this.name = user.getName();
        this.address = user.getAddress();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.number = user.getNumber();
      //  this.gps = user.getGps();
        this.key = key;
    }
}
