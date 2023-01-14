package com.pwc.sentinel;

public class Child {

    private String name, devicestatus, email;

    public Child(){

        this.name = "";
        this.devicestatus = "";
        this.email = "";
    }

    public void setName(String fname){
        this.name = fname;
    }
    public String getName(){
        return name;
    }

    public void setDevicestatus(String fstatus){
        this.devicestatus = fstatus;
    }
    public String getDevicestatus(){
        return devicestatus;
    }

    public void setEmail(String femail){
        this.email = femail;
    }
    public String getEmail(){return email;}
}
