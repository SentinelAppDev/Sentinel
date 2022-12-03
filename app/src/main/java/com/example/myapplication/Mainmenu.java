package com.example.myapplication;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

public class Mainmenu extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
         drawerLayout =findViewById(R.id.Nav_view);
        Toolbar toolbar=findViewById(R.id.toolbar);

        ActionBarDrawerToggle toggle =new ActionBarDrawerToggle( this,drawerLayout,toolbar,R.string.navigation_draw_open,R.string.navigation_draw_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}