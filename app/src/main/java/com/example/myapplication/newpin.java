package com.example.myapplication;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.goodiebag.pinview.Pinview;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Objects;

public class newpin extends AppCompatActivity {

    private boolean monitoringConnectivity = false;
    boolean isConnected = true;
    private ConstraintLayout constraint;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private String email = "", pin = "", cpin = "";
    private Pinview pb;
    private TextView instruction, instruction2;
    private Button submitBtn, confBtn;


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

        pb = (Pinview) findViewById(R.id.newPinView);
        instruction = (TextView) findViewById(R.id.newinstruction);
        instruction2 = (TextView) findViewById(R.id.newinstruction2);
        submitBtn = (Button) findViewById(R.id.newpinbtn);
        confBtn = (Button) findViewById(R.id.newpinbtn2);
        FirebaseApp.initializeApp(newpin.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Users");
        constraint = (ConstraintLayout) findViewById(R.id.constraintnewpin);

        submitBtn.setOnClickListener(view -> {

            try{

                pin = pb.getValue();
                pb.clearValue();
                instruction.setVisibility(View.GONE);
                instruction2.setVisibility(View.VISIBLE);
                submitBtn.setVisibility(View.GONE);
                confBtn.setVisibility(View.VISIBLE);


            }

            catch(Exception e){

                Log.e("onSubmitPin", e.getMessage(), e);
            }

        });

        confBtn.setOnClickListener(view -> {

            try{

                cpin = pb.getValue();
                if(Objects.equals(cpin, pin)){

                    databaseReference.child(email).child("Pin").setValue(cpin);
                    databaseReference.child(email).child("pinstatus").setValue("old");
                    Snackbar sn = Snackbar.make(constraint, "Registration Successful", Snackbar.LENGTH_SHORT);
                    sn.show();
                    startActivity(new Intent(newpin.this, MainActivity.class));
                    finish();
                }
                else {

                    pb.clearValue();
                    instruction.setVisibility(View.VISIBLE);
                    instruction2.setVisibility(View.GONE);
                    submitBtn.setVisibility(View.VISIBLE);
                    confBtn.setVisibility(View.GONE);
                }

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
