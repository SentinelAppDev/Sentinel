package com.pwc.sentinel;

public class Child {

    private String name, devicestatus, email, savedLocation, currentLocation, ms, fb, ig, yt, tw, tk;

    public Child(){

        this.name = "";
        this.devicestatus = "";
        this.email = "";
        this.savedLocation = "";
        this.currentLocation = "";
        this.ms = "";
        this.fb = "";
        this.ig = "";
        this.yt = "";
        this.tw = "";
        this.tk = "";
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

    public void setSavedLocation(String fsavedLocation){
        this.savedLocation = fsavedLocation;
    }
    public String getSavedLocation(){return savedLocation;}

    public void setCurrentLocation(String fcurrentLocation){ this.currentLocation = fcurrentLocation; }
    public String getCurrentLocation(){return currentLocation;}

    public void setMs(String fms){
        this.ms = fms;
    }
    public String getMs(){return ms;}

    public void setFb(String ffb){this.fb = ffb;}
    public String getFb(){return fb;}

    public void setIg(String fig){
        this.ig = fig;
    }
    public String getIg(){return ig;}

    public void setYt(String fyt){
        this.yt = fyt;
    }
    public String getYt(){return yt;}

    public void setTw(String ftw){
        this.tw = ftw;
    }
    public String getTw(){return tw;}

    public void setTk(String ftk){
        this.tk = ftk;
    }
    public String getTk(){return tk;}
}
