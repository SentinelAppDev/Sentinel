package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {


    boolean isConnected = true;
    private boolean monitoringConnectivity = false;
    private RelativeLayout layout;
    private FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private String pinstatus = "";
    private EditText mail, Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!CheckGpsStatus()){

            buildAlertMessageNoGps();
        }

        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){

            startActivity(new Intent(MainActivity.this, Mainmenu.class));
            finish();
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.loginlayout);

        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Users");
        mail = findViewById(R.id.Email);
        Password= findViewById(R.id.Password);
        final MaterialButton loginBtn = findViewById(R.id.loginButton);
        final TextView Signupbtn = findViewById(R.id.SignupButton);


        Signupbtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,Registration.class)));

        loginBtn.setOnClickListener(view -> {

            try {

                final String femail = mail.getText().toString().trim();
                final String fpass = Password.getText().toString().trim();

                if(TextUtils.isEmpty(femail) && TextUtils.isEmpty(fpass)){

                    mail.setError("An email is required");
                    Password.setError("A password is required");
                }

                else if(TextUtils.isEmpty(femail)) {

                    mail.setError("A username is required");
                }

                else if(TextUtils.isEmpty(fpass)) {

                    Password.setError("A password is required");
                }

                else if(!isConnected){

                    Snackbar sn = Snackbar.make(layout, "Authentication Failed, check your internet connection.", Snackbar.LENGTH_SHORT);
                    sn.show();
                }

                else {
                    getPinStatus();
                    auth.signInWithEmailAndPassword(femail, fpass)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful()){

                                        if(pinstatus.equals("new")){

                                            Intent intent = new Intent(MainActivity.this, newpin.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else if (pinstatus.equals("old")){

                                            Intent intent = new Intent(MainActivity.this, enterpin.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                    else {

                                        Snackbar sn3 = Snackbar.make(layout, "Authentication Failed, check your credentials.", Snackbar.LENGTH_SHORT);
                                        sn3.show();
                                        mail.setText(null);
                                        Password.setText(null);


                                    }
                                }
                            });
                }

            }

            catch(Exception e){

                Log.e("Login Button Clicked: ", e.getMessage(), e);

            }

        });
    }

    private void getPinStatus(){

        String email = mail.getText().toString().trim();
        String[] part = email.split("@");
        databaseReference.child(part[0]).child("pinstatus").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                pinstatus = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

    public boolean CheckGpsStatus() {

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return GpsStatus;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, please enable it to proceed.")
                .setCancelable(false)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {

                        Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(i);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private final ConnectivityManager.NetworkCallback connectivityCallback
            = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            isConnected = true;

            Snackbar snonavail = Snackbar.make(layout, "Connection Established!", Snackbar.LENGTH_SHORT);
            snonavail.show();

        }
        @Override
        public void onLost(Network network) {
            isConnected = false;

            Snackbar snonlost = Snackbar.make(layout, "Connection Lost!", Snackbar.LENGTH_INDEFINITE);
            snonlost.show();
        }
    };

    private void checkConnectivity() {

        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);

        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

        if (!isConnected) {

            Snackbar sncc = Snackbar.make(layout, "No Connection!", Snackbar.LENGTH_INDEFINITE);
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