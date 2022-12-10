package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.goodiebag.pinview.Pinview;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class enterpin extends AppCompatActivity {

    private boolean monitoringConnectivity = false;
    boolean isConnected = true;
    private ConstraintLayout constraint;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private String email = "", pin = "", cpin = "";
    private Pinview pb;
    private TextView instruction;
    private Button submitBtn;


    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_newpin);

        auth = FirebaseAuth.getInstance();
        FirebaseUser users = auth.getCurrentUser();
        if (users != null) {

            String[] c = users.getEmail().split("@");
            email = c[0];

        }

        pb = (Pinview) findViewById(R.id.oldPinView);
        instruction = (TextView) findViewById(R.id.oldinstruction);
        submitBtn = (Button) findViewById(R.id.oldpinbtn);
        FirebaseApp.initializeApp(enterpin.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Users");
        constraint = (ConstraintLayout) findViewById(R.id.constraintoldpin);

        submitBtn.setOnClickListener(view -> {

            try{

                pin = pb.getValue();
                databaseReference.child(email).child("Pin").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        try{

                            cpin = snapshot.getValue(String.class);
                            if(cpin!=null){

                                if(Objects.equals(cpin, pin)){

                                    Snackbar sn = Snackbar.make(constraint, "Success!, Identity Verified", Snackbar.LENGTH_SHORT);
                                    sn.show();
                                    Intent intent = new Intent(enterpin.this, Mainmenu.class);
                                    startActivity(intent);
                                    finish();
                                }

                                else{

                                    Snackbar sn = Snackbar.make(constraint, "PIN does not MATCH!", Snackbar.LENGTH_SHORT);
                                    sn.show();
                                    Intent intent = new Intent(enterpin.this, enterpin.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                            else {

                                Snackbar sn = Snackbar.make(constraint, "No PIN was retrieved.", Snackbar.LENGTH_SHORT);
                                sn.show();
                            }

                        }

                        catch (Exception e){

                            Log.e("on Enter Pin", e.getMessage(), e);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            catch(Exception e){

                Log.e("onSubmitPin", e.getMessage(), e);
            }

        });

    }

    public void onBackPressed(){

        super.onBackPressed();
        finish();

    }

    @Override
    protected void onResume() {

        super.onResume();
        checkConnectivity();

    }

    @Override
    protected void onPause() {

        if (monitoringConnectivity) {

            final ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.unregisterNetworkCallback(connectivityCallback);
            monitoringConnectivity = false;

        }

        super.onPause();

    }

    private final ConnectivityManager.NetworkCallback connectivityCallback
            = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            isConnected = true;

            Snackbar snonavail = Snackbar.make(constraint, "Connection Established!", Snackbar.LENGTH_SHORT);
            snonavail.show();

        }
        @Override
        public void onLost(Network network) {
            isConnected = false;

            Snackbar snonlost = Snackbar.make(constraint, "Connection Lost!", Snackbar.LENGTH_INDEFINITE);
            snonlost.show();
        }
    };

    private void checkConnectivity() {

        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);

        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

        if (!isConnected) {

            Snackbar sncc = Snackbar.make(constraint, "No Connection!", Snackbar.LENGTH_INDEFINITE);
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

}
