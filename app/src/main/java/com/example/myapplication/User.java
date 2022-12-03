package com.example.myapplication;

public class User {

    private String phone, emailaddress;

    public User(){

        this.phone = "";
        this.emailaddress = "";
    }

    public void setEmailaddress(String femail){
        this.emailaddress = femail;
    }
    public String getEmailaddress(){
        return emailaddress;
    }

    public void setPhone(String fplate){
        this.phone = fplate;
    }
    public String getPhone(){
        return phone;
    }
}
