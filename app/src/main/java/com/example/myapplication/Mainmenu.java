package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
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
        ProfileFragment.OnFragmentInteractionListener{

    private DrawerLayout drawerLayout;
    private FirebaseAuth auth;
    private String useremail = "";
    private TextView fullname, emailadd;
    private CircleImageView imageViewNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
        drawerLayout = findViewById(R.id.drawer_layout);
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

        ProfileFragment p = new ProfileFragment();
        FragmentManager f = getSupportFragmentManager();
        f.beginTransaction().replace(R.id.mainLayout, p).addToBackStack(null).commit();

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
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
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

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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

    @Override
    public void onStart(){

        super.onStart();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}