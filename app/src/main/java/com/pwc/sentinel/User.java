package com.pwc.sentinel;

public class User {

    private String phone, emailaddress, pinstatus, fullname;

    public User(){

        this.phone = "";
        this.emailaddress = "";
        this.pinstatus = "";
        this.fullname = "";
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

    public void setFullname(String fname){
        this.fullname = fname;
    }
    public String getFullname(){
        return fullname;
    }
}
