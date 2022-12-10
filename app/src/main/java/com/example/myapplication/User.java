package com.example.myapplication;

public class User {

    private String phone, emailaddress, pinstatus;

    public User(){

        this.phone = "";
        this.emailaddress = "";
        this.pinstatus = "";
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

    public void setPinstatus(String fpin){
        this.pinstatus = fpin;
    }
    public String getPinstatus(){
        return pinstatus;
    }
}
