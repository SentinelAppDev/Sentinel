package com.pwc.sentinel;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Timer;
import java.util.TimerTask;

public class SentinelService extends Service {

    private static final String TAG = SentinelService.class.getSimpleName();
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    private FirebaseAuth auth;
    private String email = "", currentLoc = "", savedLoc = "";
    private double l, lg, newl, newlg;
    private int sid;

    public SentinelService(){

    }

    public SentinelService(Context applicationContext){
        super();
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);
        sid = startId;
        auth = FirebaseAuth.getInstance();
        if(intent != null){
            String action = intent.getAction();
            switch(action){
                case ACTION_START_FOREGROUND_SERVICE:
                    showMonitoringNotification(this);
                    Log.i(TAG, "Service started");
                    FirebaseUser users = auth.getCurrentUser();
                    if (users != null)
                        email = users.getEmail();
                    startTimer();
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForeground(true);
                    stopService(new Intent(this, SentinelService.class));
                    stopSelf();
                    stopSelfResult(startId);
                    break;
            }
        }
        return START_STICKY;
    }

    private TimerTask timerTask;
    public void startTimer() {
        Timer timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 5000, 5000);
    }
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                checkLoc();
            }
        };
    }

    public void checkLoc(){
        try{
            String[] split = email.split("@");
            DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
            dr.child("Users").child(split[0]).child("Children").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        for(DataSnapshot n : snapshot.getChildren()){
                            String a = n.getKey();
                            System.out.println("Current Child: "+a);
                            getSavedLoc(a);
                            getCurrentLoc(a);
                        }
                    }
                    catch(Exception e){
                        System.err.println("On Sentinel Service [checkLoc onDataChange try..catch: retrieve child names]: "+e);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.err.println("On Sentinel Service [onCancelled: retrieve child names]: "+error);
                }
            });
        }
        catch(Exception e){
            System.err.println("On Sentinel Service [checkLoc try..catch: retrieve child names]: "+e);
        }
    }

    public void getSavedLoc(String name){
        try{
            String[] split = email.split("@");
            DatabaseReference getSLoc = FirebaseDatabase.getInstance().getReference();
            getSLoc.child("Children").child(split[0]).child(name).child("savedLocation").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        savedLoc = snapshot.getValue(String.class);
                        String[] s = savedLoc.split(",");
                        l = Double.parseDouble(s[0]);
                        lg = Double.parseDouble(s[1]);
                        System.out.println("Saved Location is: "+l+", "+lg);
                    }
                    catch(Exception e){
                        System.err.println("On Sentinel Service [getSavedLoc onDataChange try..catch: retrieve child names]: "+e);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.err.println("On Sentinel Service [onCancelled: getSavedLoc]: "+error);
                }
            });
        }
        catch(Exception e){
            System.err.println("On Sentinel Service [getSavedLoc try..catch: retrieve child names]: "+e);
        }
    }

    public void getCurrentLoc(String name){
        try{
            System.out.println("The name passed to getCurrentLoc is: "+name);
            String[] split = email.split("@");
            DatabaseReference getCLoc = FirebaseDatabase.getInstance().getReference();
            getCLoc.child("Children").child(split[0]).child(name).child("currentLocation").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        currentLoc = snapshot.getValue(String.class);
                        String[] s = currentLoc.split(",");
                        newl = Double.parseDouble(s[0]);
                        newlg = Double.parseDouble(s[1]);
                        System.out.println("Current Location is: "+newl+", "+newlg);
                        checkDistance(l, lg, newl, newlg);
                    }
                    catch(Exception e){
                        System.err.println("On Sentinel Service [getSavedLoc onDataChange try..catch: retrieve child names]: "+e);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.err.println("On Sentinel Service [onCancelled: getCurrentLoc]: "+error);
                }
            });
        }
        catch(Exception e){
            System.err.println("On Sentinel Service [getSavedLoc try..catch: retrieve child names]: "+e);
        }
    }

    public void checkDistance(double ilat, double ilng, double flat, double flng){

        Location me   = new Location("");
        Location dest = new Location("");

        me.setLatitude(ilat);
        me.setLongitude(ilng);

        dest.setLatitude(flat);
        dest.setLongitude(flng);
        System.out.println("The distance is: "+me.distanceTo(dest));
        if(me.distanceTo(dest) > 200)
            showNotification(getApplicationContext());
    }

    public void showNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = 2;
        String channelId = "channel-02";
        String channelName = "Notif2";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(
                channelId, channelName, importance);
        notificationManager.createNotificationChannel(mChannel);
        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("Sentinel")
                .setContentText("Your child might be entering or leaving his/her location")
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setSmallIcon(R.drawable.sentinel).build();
        notificationManager.notify(notificationId, notification);
    }

    public void showMonitoringNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Notif1";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(
                channelId, channelName, importance);
        notificationManager.createNotificationChannel(mChannel);
        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("Sentinel")
                .setContentText("Sentinel is active.")
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setSmallIcon(R.drawable.sentinel).build();
        startForeground(notificationId, notification);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // TODO Auto-generated method stub
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        restartService.setAction(SentinelService.ACTION_START_FOREGROUND_SERVICE);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +100, restartServicePI);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        stopService(new Intent(this, SentinelService.class));
        stopSelf();
        stopSelfResult(sid);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
