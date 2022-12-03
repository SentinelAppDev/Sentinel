package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Registration extends AppCompatActivity {

    private FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase, fd;
    DatabaseReference databaseReference, dr;
    private boolean monitoringConnectivity = false;
    private boolean isConnected = true;
    private ConstraintLayout layout;
    private EditText phone, email, password, confirm;
    private Button regBtn;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);
        auth = FirebaseAuth.getInstance();
        layout = findViewById(R.id.reglayout);
        phone = findViewById(R.id.phoneNum);
        email = findViewById(R.id.emailReg);
        password = findViewById(R.id.Pw1Reg);
        confirm = findViewById(R.id.Pw2Reg);
        regBtn = findViewById(R.id.ReAcbtn);
        FirebaseApp.initializeApp(Registration.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        fd = FirebaseDatabase.getInstance();
        dr = fd.getReference();

        regBtn.setOnClickListener(v -> {

            try{

                final String fphone = phone.getText().toString().trim();
                final String femail = email.getText().toString().trim();
                final String fpass = password.getText().toString().trim();
                final String fconf = confirm.getText().toString().trim();

                if (TextUtils.isEmpty(femail) && TextUtils.isEmpty(fphone) && TextUtils.isEmpty(fpass) && TextUtils.isEmpty(fconf)){

                    phone.setError("A mobile number is required.");
                    email.setError("An email address is required.");
                    password.setError("A password is required.");
                }
                else if(TextUtils.isEmpty(femail)){
                    email.setError("An email address is required.");
                }
                else if(TextUtils.isEmpty(fphone)){
                    phone.setError("A mobile number is required.");
                }
                else if(TextUtils.isEmpty(fpass)){
                    password.setError("A password is required.");
                }
                else if(TextUtils.isEmpty(fconf)){
                    confirm.setError("Please confirm your password.");
                }
                else if (!fpass.equals(fconf)){

                    password.setError("Passwords do not match!");
                    confirm.setError("Passwords do not match!");
                    password.setText(null);
                    confirm.setText(null);
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(femail).matches()) {

                    email.setError("Please input a valid email address");
                }
                else if (fpass.length() < 8) {

                    password.setError("Password is too short, input at least 8 characters");

                }
                else{

                    AlertDialog.Builder builder = new AlertDialog.Builder(Registration.this);
                    View root = getLayoutInflater().inflate((R.layout.dialogconfirmsignup), null);
                    TextView emailadd = root.findViewById(R.id.confirmcontact);
                    TextView phonenumber = root.findViewById(R.id.confirmemail);
                    emailadd.setText(femail);
                    phonenumber.setText(fphone);
                    builder.setView(root);
                    builder.setPositiveButton("Confirm", (dialogInterface, i) -> auth.createUserWithEmailAndPassword(femail, fpass)
                            .addOnCompleteListener(Registration.this, task -> {

                                if (!task.isSuccessful()) {

                                    Log.w("Registration", "signInWithCredential", task.getException());
                                    Snackbar sn = Snackbar.make(layout, "Signup has failed.", Snackbar.LENGTH_SHORT);
                                    sn.show();

                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                                        Snackbar sn1 = Snackbar.make(layout, "Account already exists", Snackbar.LENGTH_SHORT);
                                        sn1.show();
                                        email.setText(null);
                                        password.setText(null);
                                        confirm.setText(null);
                                        phone.setText(null);

                                    } else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {

                                        Snackbar sn2 = Snackbar.make(layout, "Please input a stronger password", Snackbar.LENGTH_SHORT);
                                        sn2.show();
                                        password.setText(null);

                                    } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                        Snackbar sn3 = Snackbar.make(layout, "Please input a valid email address", Snackbar.LENGTH_SHORT);
                                        sn3.show();
                                        email.setText(null);
                                        password.setText(null);
                                        confirm.setText(null);
                                        phone.setText(null);

                                    }


                                } else if (task.isSuccessful()) {

                                    createUser(femail, fphone);
                                    auth.signOut();
                                    Snackbar sn4 = Snackbar.make(layout, "Registration Successful", Snackbar.LENGTH_SHORT);
                                    sn4.show();
                                    startActivity(new Intent(Registration.this, MainActivity.class));
                                    finish();

                                }
                            }));

                    builder.setNegativeButton("Cancel", (dialog, which) -> {

                        dialog.dismiss();
                        email.setText(null);
                        password.setText(null);
                        confirm.setText(null);
                        phone.setText(null);

                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }

            catch (Exception e) {
                Log.e("On Signup Button Clicked: ", e.getMessage(), e);
            }

        });

    }

    private void createUser(String email, String phone){

        User u = new User();
        u.setEmailaddress(email);
        u.setPhone(phone);

        String[] parts = email.split("@");
        databaseReference.child("Users").child(parts[0]).setValue(u)
                .addOnSuccessListener(aVoid -> {

                    Log.d("FirebaseData","user data uploaded successfully");

                })
                .addOnFailureListener(e -> {

                    Log.d("FirebaseData","user data upload failed");
                    Toast.makeText(Registration.this, e.getMessage(), Toast.LENGTH_LONG).show();

                });
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