package com.pwc.sentinel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class Mainmenu extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        ProfileFragment.OnFragmentInteractionListener,
        AddChildFragment.OnFragmentInteractionListener,
        MapFragment.OnFragmentInteractionListener,
        DeviceFragment.OnFragmentInteractionListener{

    private DrawerLayout drawerLayout;
    private FirebaseAuth auth;
    private String useremail = "";
    private TextView fullname, emailadd;
    private CircleImageView imageViewNav;
    boolean isConnected = true;
    private boolean monitoringConnectivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
        drawerLayout = findViewById(R.id.drawer_layout);

        SentinelService services = new SentinelService(this);
        Intent mServiceIntent = new Intent(Mainmenu.this, SentinelService.class);
        mServiceIntent.setAction(SentinelService.ACTION_START_FOREGROUND_SERVICE);
        if (!isMyServiceRunning(services.getClass()))
            ContextCompat.startForegroundService(this, mServiceIntent);

        auth = FirebaseAuth.getInstance();
        FirebaseUser users = auth.getCurrentUser();
        if (users != null) {

            useremail = users.getEmail();

        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        fullname = (TextView) header.findViewById(R.id.usersname);
        emailadd = (TextView) header.findViewById(R.id.usersemail);
        imageViewNav = (CircleImageView)header.findViewById(R.id.profilepic);
        String[] app = useremail.split("@");
        getName(app[0]);
        getProfilePic(app[0]);

        ProfileFragment p = new ProfileFragment();
        FragmentManager f = getSupportFragmentManager();
        f.beginTransaction().replace(R.id.mainLayout, p).addToBackStack(null).commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.Logout){
            AlertDialog.Builder builder = new AlertDialog.Builder(Mainmenu.this);
            builder.setTitle("Please confirm action!");
            builder.setMessage("Are you sure you want to Logout?");
            builder.setIcon(R.drawable.sentinel);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    signOut();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Toast.makeText(getApplicationContext(), "Cancelled!", Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        else if(id == R.id.Mprfle){
            ProfileFragment p = new ProfileFragment();
            FragmentManager f = getSupportFragmentManager();
            f.beginTransaction().replace(R.id.mainLayout, p).addToBackStack(null).commit();
        }

        else if(id == R.id.AdChld){
            AddChildFragment p = new AddChildFragment();
            FragmentManager f = getSupportFragmentManager();
            f.beginTransaction().replace(R.id.mainLayout, p).addToBackStack(null).commit();
        }
        else if(id == R.id.ChldLoc){
            MapFragment p = new MapFragment();
            FragmentManager f = getSupportFragmentManager();
            f.beginTransaction().replace(R.id.mainLayout, p).addToBackStack(null).commit();
        }
        else if(id == R.id.Blck){
            DeviceFragment p = new DeviceFragment();
            FragmentManager f = getSupportFragmentManager();
            f.beginTransaction().replace(R.id.mainLayout, p).addToBackStack(null).commit();
        }
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getProfilePic(String email){

        String[] split = email.split("@");
        DatabaseReference g = FirebaseDatabase.getInstance().getReference().child("Users").child(split[0]).child("ProfilePicture");
        g.child("imageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String url = snapshot.getValue(String.class);
                Glide.with(Mainmenu.this).load(url).into(imageViewNav);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println("on getProfilePic (ProfileFragment): "+error);
            }
        });
    }

    public void signOut() {

        try{

            auth.signOut();
            startActivity(new Intent(Mainmenu.this, MainActivity.class));
            finish();

        }
        catch(Exception e){

            Log.e("onSignOut", e.getMessage(), e);

        }
    }

    public void getName(String email){

        try{

            DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Users");
            dr.child(email).child("fullname").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String a = snapshot.getValue(String.class);
                    fullname.setText(a);
                    emailadd.setText(useremail);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.err.println(error);
                }
            });
        }

        catch(Exception e){

            Log.e("getName method: ", e.getMessage(), e);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private final ConnectivityManager.NetworkCallback connectivityCallback
            = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            isConnected = true;

            Snackbar snonavail = Snackbar.make(drawerLayout, "Connection Established!", Snackbar.LENGTH_SHORT);
            snonavail.show();

        }
        @Override
        public void onLost(Network network) {
            isConnected = false;

            Snackbar snonlost = Snackbar.make(drawerLayout, "Connection Lost!", Snackbar.LENGTH_INDEFINITE);
            snonlost.show();
        }
    };

    private void checkConnectivity() {

        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);

        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

        if (!isConnected) {

            Snackbar sncc = Snackbar.make(drawerLayout, "No Connection!", Snackbar.LENGTH_INDEFINITE);
            sncc.show();
            connectivityManager.registerNetworkCallback(
                    new NetworkRequest.Builder()
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .build(), connectivityCallback);
            monitoringConnectivity = true;
        }
        else {
            connectivityManager.registerNetworkCallback(
                    new NetworkRequest.Builder()
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .build(), connectivityCallback);
            monitoringConnectivity = true;
        }
    }

    @Override
    public void onStart(){

        super.onStart();
        checkConnectivity();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onResume() {

        checkConnectivity();
        super.onResume();

    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        }
        else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}